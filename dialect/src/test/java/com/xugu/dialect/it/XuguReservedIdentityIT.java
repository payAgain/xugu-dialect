package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
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
import com.xugu.dialect.it.entities.I004P002OrderEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * I-004 / P-002: IDENTITY persist into reserved-word table {@code "select"} must
 * backfill id without JDBC {@code RETURN_GENERATED_KEYS} re-parse errors
 * (e.g. {@code unexpected SELECT} / {@code unexpected ORDER} on reserved names).
 *
 * <p>Table is {@code "select"} (not {@code "order"}) to avoid colliding with demo/app
 * {@code Order} tables that block DROP and make {@code CREATE IF NOT EXISTS} a no-op.
 */
class XuguReservedIdentityIT {

	/** Quoted reserved identifier — matches Dialect openQuote/closeQuote '"'. */
	private static final String QUOTED_RESERVED_TABLE = "\"select\"";

	@Test
	void identityPersistOnReservedTableOrderBackfillsId() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I004P002OrderEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );

			assertReservedTableHasNameColumn();

			sf = metadata.buildSessionFactory();
			Integer id;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				I004P002OrderEntity e = new I004P002OrderEntity( "reserved-select-1" );
				session.persist( e );
				session.flush();
				id = e.getId();
				assertNotNull( id, "IDENTITY id must be backfilled after persist/flush on reserved table" );
				assertTrue( id > 0, "expected positive identity id, got " + id );
				session.getTransaction().commit();
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			String msg = rootMessage( e );
			assertFalse( msg.toUpperCase( Locale.ROOT ).contains( "UNEXPECTED SELECT" )
							|| msg.toUpperCase( Locale.ROOT ).contains( "UNEXPECTED \"SELECT\"" )
							|| msg.toUpperCase( Locale.ROOT ).contains( "UNEXPECTED ORDER" )
							|| msg.toUpperCase( Locale.ROOT ).contains( "UNEXPECTED \"ORDER\"" ),
					"must not fail with reserved-word parse error: " + msg );
			fail( "Reserved IDENTITY IT failed: " + msg, e );
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
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				// Do not force USE_GET_GENERATED_KEYS=true — dialect default false is the fix
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, a -> {
		} );
	}

	/**
	 * Guard against CREATE IF NOT EXISTS colliding with an unrelated table of the same
	 * unquoted name (live symptom: E16007 字段NAME不存在 on insert).
	 */
	private static void assertReservedTableHasNameColumn() throws Exception {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement();
			ResultSet rs = st.executeQuery( "select name from " + QUOTED_RESERVED_TABLE + " where 1=0" ) ) {
			assertNotNull( rs.getMetaData() );
		}
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			ignore( st, "DROP TABLE IF EXISTS " + QUOTED_RESERVED_TABLE );
		}
		catch ( Exception ignored ) {
		}
	}

	private static void ignore(Statement st, String sql) {
		try {
			st.execute( sql );
		}
		catch ( Exception ignored ) {
		}
	}

	private static String rootMessage(Throwable t) {
		Throwable cur = t;
		String last = t.getMessage();
		while ( cur != null ) {
			if ( cur.getMessage() != null && !cur.getMessage().isBlank() ) {
				last = cur.getMessage();
			}
			cur = cur.getCause();
		}
		return last == null ? t.toString() : last;
	}
}
