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
import org.hibernate.cfg.QuerySettings;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: representative HQL/native functions per family on live XuguDB (P-006).
 */
class XuguFunctionRegistryIT {

	private static final String TABLE = "HIB_P006_FUN";

	@Test
	void functionFamilies_HqlAndNative_A_FUN() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanup();

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
				P006FunEntity a = new P006FunEntity();
				a.setId( 1 );
				a.setName( "Alice" );
				a.setAmount( 10 );
				P006FunEntity b = new P006FunEntity();
				b.setId( 2 );
				b.setName( "Bob" );
				b.setAmount( -3 );
				session.persist( a );
				session.persist( b );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				// String: concat, substring, lower
				String concat = session.createQuery(
						"select concat(e.name, '-x') from P006FunEntity e where e.id = 1", String.class )
						.getSingleResult();
				assertEquals( "Alice-x", concat );

				String sub = session.createQuery(
						"select substring(e.name, 1, 3) from P006FunEntity e where e.id = 1", String.class )
						.getSingleResult();
				assertEquals( "Ali", sub );

				String lower = session.createQuery(
						"select lower(e.name) from P006FunEntity e where e.id = 1", String.class )
						.getSingleResult();
				assertEquals( "alice", lower );

				// Math: abs
				Integer abs = session.createQuery(
						"select abs(e.amount) from P006FunEntity e where e.id = 2", Integer.class )
						.getSingleResult();
				assertEquals( 3, abs );

				// Temporal: current_timestamp + extract/year
				Object ts = session.createQuery( "select current_timestamp", Object.class ).getSingleResult();
				assertNotNull( ts );

				Integer year = session.createQuery(
						"select extract(year from current_date)", Integer.class ).getSingleResult();
				assertTrue( year >= 2020, "year=" + year );

				// Cast
				String casted = session.createQuery(
						"select cast(e.amount as string) from P006FunEntity e where e.id = 1", String.class )
						.getSingleResult();
				assertEquals( "10", casted );

				// Aggregates: count / sum
				Long count = session.createQuery( "select count(e) from P006FunEntity e", Long.class )
						.getSingleResult();
				assertEquals( 2L, count );
				Long sum = session.createQuery( "select sum(e.amount) from P006FunEntity e", Long.class )
						.getSingleResult();
				assertEquals( 7L, sum );

				// UUID primary
				String uuid = session.createQuery( "select uuid()", String.class ).getSingleResult();
				assertNotNull( uuid );
				assertTrue( uuid.contains( "-" ), "primary uuid() should be dashed: " + uuid );

				// JSON subset
				String jv = session.createQuery(
						"select json_value('{\"a\":1}', '$.a')", String.class ).getSingleResult();
				assertEquals( "1", jv );

				// listagg (HQL ordered-set aggregate)
				String listagg = session.createQuery(
						"select listagg(e.name, ',') within group (order by e.name) from P006FunEntity e",
						String.class ).getSingleResult();
				assertTrue( listagg.contains( "Alice" ) && listagg.contains( "Bob" ), "listagg=" + listagg );
			}

			// Native probes for json_extract + dual forms
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				try ( ResultSet rs = st.executeQuery(
						"SELECT JSON_EXTRACT('{\"a\":1}', '$.a') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertNotNull( rs.getObject( 1 ) );
				}
				try ( ResultSet rs = st.executeQuery( "SELECT UUID() FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertNotNull( rs.getObject( 1 ) );
				}
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			fail( "Function IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	@Test
	void unsupportedFunctionNegative_diagnosable() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.executeQuery( "SELECT xugu_unsupported_fn_xyz() FROM DUAL" );
			fail( "expected unsupported function to fail" );
		}
		catch ( Exception e ) {
			String all = ( e.getMessage() + " " + ( e.getCause() == null ? "" : e.getCause().getMessage() ) )
					.toLowerCase();
			assertTrue(
					all.contains( "xugu_unsupported" )
							|| all.contains( "function" )
							|| all.contains( "unknown" )
							|| all.contains( "不存在" )
							|| all.contains( "未找到" )
							|| all.contains( "error" )
							|| all.contains( "e10" )
							|| all.contains( "e50" ),
					"diagnosable failure expected, got: " + e );
		}
	}

	private static StandardServiceRegistry buildRegistry() {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.applySetting( JdbcSettings.FORMAT_SQL, "true" )
				// Hibernate 7 treats HQL json_* as tech-preview; enable for documented subset IT
				.applySetting( QuerySettings.JSON_FUNCTIONS_ENABLED, "true" )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, actionCompleted -> {
		} );
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			try {
				st.execute( "DROP TABLE " + TABLE );
			}
			catch ( Exception ignore ) {
				// absent is fine
			}
		}
		catch ( Exception ignore ) {
			// gate may be on but connection later
		}
	}
}
