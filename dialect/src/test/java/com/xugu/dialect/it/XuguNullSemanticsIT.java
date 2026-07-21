package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
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
import com.xugu.dialect.it.entities.I010P016NullEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: null-semantics subset (XP-008) — IS NULL / IS NOT NULL / three-valued /
 * coalesce (A-FUN-007).
 *
 * <p>I-010/P-016 — 4 focused cases; coalesce only (no undocumented null functions).
 */
class XuguNullSemanticsIT {

	private static final String TABLE = "HIB_I010_P016_NULL";

	/** IS NULL on nullable String hits seeded null rows. */
	@Test
	void isNullQueryHitsNullableString() {
		runNullCases( session -> {
			List<Integer> ids = session.createQuery(
					"select e.id from I010P016NullEntity e where e.name is null order by e.id",
					Integer.class )
					.getResultList();
			assertEquals( List.of( 1, 3 ), ids );
		} );
	}

	/** IS NOT NULL on nullable String hits non-null row. */
	@Test
	void isNotNullQueryHitsPresentString() {
		runNullCases( session -> {
			List<Integer> ids = session.createQuery(
					"select e.id from I010P016NullEntity e where e.name is not null order by e.id",
					Integer.class )
					.getResultList();
			assertEquals( List.of( 2 ), ids );
		} );
	}

	/**
	 * Three-valued: equality to a null bind parameter must not match (including null columns).
	 * Companion: HQL {@code is null} remains the positive null predicate.
	 */
	@Test
	void threeValuedEqualsNullParamDoesNotHit() {
		runNullCases( session -> {
			List<Integer> eqNullParam = session.createQuery(
					"select e.id from I010P016NullEntity e where e.name = :p",
					Integer.class )
					.setParameter( "p", null )
					.getResultList();
			assertTrue( eqNullParam.isEmpty(), "e.name = :null must not hit under three-valued logic: " + eqNullParam );

			List<Integer> isNullIds = session.createQuery(
					"select e.id from I010P016NullEntity e where e.qty is null order by e.id",
					Integer.class )
					.getResultList();
			assertEquals( List.of( 1 ), isNullIds );
		} );
	}

	/** A-FUN-007: coalesce projection for null and non-null name. */
	@Test
	void coalesceProjectionFallbackAndPresent() {
		runNullCases( session -> {
			String fromNull = session.createQuery(
					"select coalesce(e.name, 'x') from I010P016NullEntity e where e.id = 1",
					String.class )
					.getSingleResult();
			assertEquals( "x", fromNull );

			String fromPresent = session.createQuery(
					"select coalesce(e.name, 'x') from I010P016NullEntity e where e.id = 2",
					String.class )
					.getSingleResult();
			assertEquals( "Alice", fromPresent );
		} );
	}

	private static void runNullCases(Consumer<Session> body) {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P016NullEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P016NullEntity( 1, null, null ) );
				session.persist( new I010P016NullEntity( 2, "Alice", 10 ) );
				session.persist( new I010P016NullEntity( 3, null, 5 ) );
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
			fail( "Null semantics IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
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

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
		}
		catch ( Exception ignored ) {
		}
	}
}
