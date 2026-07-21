package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: independent HQL {@code Session.createQuery} live positives for Batch A
 * families that previously relied on thin adjacent-live (A-FUN-001/004/010 bundle).
 *
 * <p>I-010/P-008 — each family has its own method (not a shared smoke). Reuses
 * {@link P006FunEntity} for SessionFactory bootstrap.
 */
class XuguBatchAFunctionFamiliesIT {

	private static final String FUN_ENTITY_TABLE = "HIB_P006_FUN";

	/** A-FUN-003: length / related (character_length). */
	@Test
	void lengthFamilyHqlSession_A_FUN_003() {
		runFamily( session -> {
			Integer lenLiteral = session.createQuery(
					"select length('Alice')", Integer.class )
					.getSingleResult();
			assertEquals( 5, lenLiteral.intValue() );

			Integer lenEntity = session.createQuery(
					"select length(e.name) from P006FunEntity e where e.id = 1",
					Integer.class )
					.getSingleResult();
			assertEquals( 5, lenEntity.intValue() );

			// related: length over concat (same family surface; char_length is registry-unit)
			Integer lenConcat = session.createQuery(
					"select length(concat(e.name, '!')) from P006FunEntity e where e.id = 1",
					Integer.class )
					.getSingleResult();
			assertEquals( 6, lenConcat.intValue() );
		} );
	}

	/** A-FUN-005: trim / ltrim / rtrim. */
	@Test
	void trimFamilyHqlSession_A_FUN_005() {
		runFamily( "  Alice  ", 0, session -> {
			String trimmed = session.createQuery(
					"select trim(e.name) from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "Alice", trimmed );

			String ltrimmed = session.createQuery(
					"select ltrim(e.name) from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertTrue( ltrimmed.startsWith( "Alice" ), "ltrim=" + ltrimmed );
			assertTrue( ltrimmed.endsWith( "  " ) || ltrimmed.equals( "Alice  " ),
					"ltrim should keep trailing spaces: " + ltrimmed );

			String rtrimmed = session.createQuery(
					"select rtrim(e.name) from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertTrue( rtrimmed.endsWith( "Alice" ), "rtrim=" + rtrimmed );
			assertTrue( rtrimmed.startsWith( "  " ) || rtrimmed.equals( "  Alice" ),
					"rtrim should keep leading spaces: " + rtrimmed );

			String trimLiteral = session.createQuery(
					"select trim('  x  ')", String.class )
					.getSingleResult();
			assertEquals( "x", trimLiteral );
		} );
	}

	/** A-FUN-006: replace / locate (position related via registry unit). */
	@Test
	void replaceLocateHqlSession_A_FUN_006() {
		runFamily( session -> {
			String replaced = session.createQuery(
					"select replace(e.name, 'ice', 'icia') from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "Alicia", replaced );

			Integer loc = session.createQuery(
					"select locate('li', e.name) from P006FunEntity e where e.id = 1",
					Integer.class )
					.getSingleResult();
			assertTrue( loc >= 1, "locate('li','Alice')=" + loc );

			String replaceLiteral = session.createQuery(
					"select replace('foo-bar', '-', '_')", String.class )
					.getSingleResult();
			assertEquals( "foo_bar", replaceLiteral );
		} );
	}

	/** A-FUN-007: coalesce / nvl. */
	@Test
	void coalesceNvlHqlSession_A_FUN_007() {
		runFamily( session -> {
			String coalesced = session.createQuery(
					"select coalesce(e.name, 'fallback') from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "Alice", coalesced );

			// null path via nullif
			String coalesceNull = session.createQuery(
					"select coalesce(nullif(e.name, e.name), 'fallback') from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "fallback", coalesceNull );

			String nvlPresent = session.createQuery(
					"select nvl(e.name, 'fallback') from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "Alice", nvlPresent );

			String nvlNull = session.createQuery(
					"select nvl(nullif(e.name, e.name), 'fallback') from P006FunEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "fallback", nvlNull );
		} );
	}

	/** A-FUN-009: round / trunc. */
	@Test
	void roundTruncHqlSession_A_FUN_009() {
		runFamily( "Alice", 0, session -> {
			Number rounded = session.createQuery(
					"select round(1.4)", Number.class )
					.getSingleResult();
			assertNotNull( rounded );
			assertEquals( 1, rounded.intValue() );

			Number roundedUp = session.createQuery(
					"select round(1.6)", Number.class )
					.getSingleResult();
			assertNotNull( roundedUp );
			assertEquals( 2, roundedUp.intValue() );

			Number truncated = session.createQuery(
					"select trunc(1.9)", Number.class )
					.getSingleResult();
			assertNotNull( truncated );
			assertEquals( 1, truncated.intValue() );

			// entity numeric path (amount)
			Number truncAmount = session.createQuery(
					"select trunc(e.amount) from P006FunEntity e where e.id = 1",
					Number.class )
					.getSingleResult();
			assertNotNull( truncAmount );
			assertEquals( 0, truncAmount.intValue() );
		} );
	}

	private static void runFamily(Consumer<Session> body) {
		runFamily( "Alice", 10, body );
	}

	private static void runFamily(String name, int amount, Consumer<Session> body) {
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
				row.setName( name );
				row.setAmount( amount );
				session.persist( row );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				body.accept( session );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Batch A HQL Session IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanupFunEntityTable();
		}
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
