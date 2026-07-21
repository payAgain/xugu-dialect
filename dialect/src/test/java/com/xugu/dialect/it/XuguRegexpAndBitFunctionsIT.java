package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.P006FunEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-FUN-019 regexp_* + A-FUN-015 bit_and/bit_or native SQL.
 *
 * <p>SQL shapes from {@code reference/function/string-functions/regexp_*.md} and
 * {@code reference/function/aggregate-functions/bit_*.md}. Bit aggregates use {@code VARBIT}
 * columns per {@code reference/sql/datatype/bit.md} — not verified via ORM entity mapping.
 *
 * <p>HQL Session path (I-010/P-007 / A-FUN-019): {@code Session.createQuery} positive
 * for registered {@code regexp_like}/{@code regexp_replace}/{@code regexp_substr} only
 * (same shapes as native subset). Does <em>not</em> claim unregistered aliases
 * (e.g. {@code regexp_instr}).
 */
class XuguRegexpAndBitFunctionsIT {

	private static final String BIT_AND_TABLE = "HIB_I009_P006_BIT_AND";
	private static final String BIT_OR_TABLE = "HIB_I009_P006_BIT_OR";
	private static final String FUN_ENTITY_TABLE = "HIB_P006_FUN";

	/**
	 * I-010/P-007: HQL {@code Session.createQuery} live positive for registered regexp
	 * subset. Reuses {@link P006FunEntity} SessionFactory bootstrap (literal selects;
	 * entity row optional for Session wiring). Native subset retained separately.
	 */
	@Test
	void regexpFunctionsHqlSession_A_FUN_019() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanupFunEntityTable();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P006FunEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				P006FunEntity row = new P006FunEntity();
				row.setId( 1 );
				row.setName( "England or America" );
				row.setAmount( 0 );
				session.persist( row );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				// regexp_like.md — example 1
				Object likeTrue = session.createQuery(
						"select regexp_like('England or America', 'l.nd')", Object.class )
						.getSingleResult();
				assertTrue( isTruthyObject( likeTrue ), "regexp_like HQL England: " + likeTrue );

				// regexp_like.md — example 2 (match mode)
				Object likeFalse = session.createQuery(
						"select regexp_like('MCA', 'BCA', 'inx')", Object.class )
						.getSingleResult();
				assertFalse( isTruthyObject( likeFalse ), "regexp_like HQL MCA inx: " + likeFalse );

				// entity column via HQL (registry + Session path)
				Object likeEntity = session.createQuery(
						"select regexp_like(e.name, 'l.nd') from P006FunEntity e where e.id = 1",
						Object.class )
						.getSingleResult();
				assertTrue( isTruthyObject( likeEntity ), "regexp_like entity HQL: " + likeEntity );

				// regexp_replace.md — example 1: date reorder
				String replaced = session.createQuery(
						"select regexp_replace('2023-08-01', '(\\d{4})-(\\d{2})-(\\d{2})', '\\2/\\3/\\1')",
						String.class )
						.getSingleResult();
				assertNotNull( replaced );
				assertEquals( "08/01/2023", replaced.trim() );

				// regexp_replace.md — example 2: mask digits
				String masked = session.createQuery(
						"select regexp_replace('1234567890', '\\d(?=\\d{4})', '*')",
						String.class )
						.getSingleResult();
				assertNotNull( masked );
				assertEquals( "******7890", masked.trim() );

				// regexp_substr.md — example 1
				String substr = session.createQuery(
						"select regexp_substr('订单ID: 789, 数量: 456', '[0-9]+')",
						String.class )
						.getSingleResult();
				assertNotNull( substr );
				assertEquals( "789", substr.trim() );

				// regexp_substr.md — example 2: start + occurrence
				String substrOcc = session.createQuery(
						"select regexp_substr('a1,b2,c3,d4', '[^,]+', 5, 2)",
						String.class )
						.getSingleResult();
				assertNotNull( substrOcc );
				assertEquals( "c3", substrOcc.trim() );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Regexp HQL Session IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanupFunEntityTable();
		}
	}

	@Test
	void regexpFunctionsNativeSubset_A_FUN_019() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			try ( Statement st = c.createStatement() ) {

				// regexp_like.md — example 1: 'England or America' ~ 'l.nd' → T
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_LIKE('England or America', 'l.nd') FROM DUAL" ) ) {
					assertTrue( rs.next(), "REGEXP_LIKE row expected" );
					assertTrue( isTruthy( rs, 1 ), "REGEXP_LIKE England: " + rs.getObject( 1 ) );
				}

				// regexp_like.md — example 2: 'MCA' ~ 'BCA' with 'inx' → F
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_LIKE('MCA', 'BCA', 'inx') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertFalse( isTruthy( rs, 1 ), "REGEXP_LIKE MCA inx: " + rs.getObject( 1 ) );
				}

				// regexp_replace.md — example 1: date reorder
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_REPLACE('2023-08-01', '(\\d{4})-(\\d{2})-(\\d{2})', '\\2/\\3/\\1') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "08/01/2023", rs.getString( 1 ).trim() );
				}

				// regexp_replace.md — example 2: mask digits
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_REPLACE('1234567890', '\\d(?=\\d{4})', '*') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "******7890", rs.getString( 1 ).trim() );
				}

				// regexp_substr.md — example 1: first digit run
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_SUBSTR('订单ID: 789, 数量: 456', '[0-9]+') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "789", rs.getString( 1 ).trim() );
				}

				// regexp_substr.md — example 2: start=5, occurrence=2
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_SUBSTR('a1,b2,c3,d4', '[^,]+', 5, 2) FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "c3", rs.getString( 1 ).trim() );
				}
			}
		}
		catch ( Exception e ) {
			fail( "Regexp functions native IT failed: " + e.getMessage(), e );
		}
	}

	@Test
	void bitAggregatesNativeSubset_A_FUN_015() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + BIT_AND_TABLE );
				st.execute( "CREATE TABLE " + BIT_AND_TABLE + " (c1 VARBIT(7))" );
				st.execute( "INSERT INTO " + BIT_AND_TABLE + " VALUES (b'1010101')(b'1011100')(b'0011100')" );
				try ( ResultSet rs = st.executeQuery( "SELECT BIT_AND(c1) FROM " + BIT_AND_TABLE ) ) {
					assertTrue( rs.next(), "BIT_AND row expected" );
					assertBitLiteralContains( rs.getString( 1 ), "0010100" );
				}

				st.execute( "DROP TABLE IF EXISTS " + BIT_OR_TABLE );
				st.execute( "CREATE TABLE " + BIT_OR_TABLE + " (c1 VARBIT(7))" );
				st.execute( "INSERT INTO " + BIT_OR_TABLE + " VALUES (b'1010101')(b'1011100')(b'0011100')" );
				try ( ResultSet rs = st.executeQuery( "SELECT BIT_OR(c1) FROM " + BIT_OR_TABLE ) ) {
					assertTrue( rs.next(), "BIT_OR row expected" );
					assertBitLiteralContains( rs.getString( 1 ), "1011101" );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "DROP TABLE IF EXISTS " + BIT_AND_TABLE );
					st.execute( "DROP TABLE IF EXISTS " + BIT_OR_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "Bit aggregate native IT failed: " + e.getMessage(), e );
		}
	}

	private static boolean isTruthy(ResultSet rs, int column) throws Exception {
		return isTruthyObject( rs.getObject( column ) );
	}

	private static boolean isTruthyObject(Object value) {
		if ( value instanceof Boolean b ) {
			return b;
		}
		if ( value instanceof Number n ) {
			return n.intValue() != 0;
		}
		String s = String.valueOf( value ).trim();
		return "T".equalsIgnoreCase( s ) || "TRUE".equalsIgnoreCase( s ) || "1".equals( s );
	}

	private static void assertBitLiteralContains(String actual, String expectedBits) {
		String normalized = actual == null ? "" : actual.replaceAll( "[^01]", "" );
		assertEquals( expectedBits, normalized, "bit literal: " + actual );
	}

	private static StandardServiceRegistry buildRegistry() {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, completion -> {
		} );
	}

	private static void cleanupFunEntityTable() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + FUN_ENTITY_TABLE );
		}
		catch ( Exception ignored ) {
		}
	}
}
