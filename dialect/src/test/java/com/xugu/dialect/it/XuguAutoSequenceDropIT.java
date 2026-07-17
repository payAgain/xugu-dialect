package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.xugu.dialect.it.entities.I004P001AutoEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * I-004 / P-001: {@code GenerationType.AUTO} schema drop must be idempotent
 * ({@code DROP SEQUENCE IF EXISTS}) so missing sequences do not fail with E7002.
 */
class XuguAutoSequenceDropIT {

	private static final String TABLE = "HIB_I004_P001_AUTO";
	private static final String SEQ_PREFIX = "HIB_I004_P001";

	@Test
	void schemaDropWhenAutoSequenceAbsentDoesNotFailE7002() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry( "none" );
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I004P001AutoEntity.class )
					.buildMetadata();

			export( metadata, registry, Action.CREATE_ONLY );

			List<String> sequences = listMatchingSequences();
			assertFalse( sequences.isEmpty(),
					"expected AUTO to create at least one physical sequence matching " + SEQ_PREFIX );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				for ( String seq : sequences ) {
					st.execute( "DROP SEQUENCE IF EXISTS " + seq );
				}
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			String msg = rootMessage( e );
			assertFalse( msg.toUpperCase( Locale.ROOT ).contains( "E7002" ),
					"schema drop must not fail with E7002 when sequence absent: " + msg );
			fail( "schema drop when sequence absent failed: " + msg, e );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	@Test
	void sessionFactoryCreateDropBuildsWhenSequenceInitiallyAbsent() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry( "create-drop" );
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I004P001AutoEntity.class )
					.buildMetadata();
			sf = metadata.buildSessionFactory();
			assertTrue( sf.isOpen(), "SessionFactory should build with create-drop on clean DB" );
		}
		catch ( Exception e ) {
			String msg = rootMessage( e );
			assertFalse( msg.toUpperCase( Locale.ROOT ).contains( "E7002" ),
					"SessionFactory create-drop must not fail with E7002: " + msg );
			fail( "SessionFactory create-drop failed: " + msg, e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	private static StandardServiceRegistry buildRegistry(String hbm2ddlAuto) {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, hbm2ddlAuto )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, completion -> {
		} );
	}

	private static List<String> listMatchingSequences() throws Exception {
		List<String> names = new ArrayList<>();
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT SEQ_NAME FROM ALL_SEQUENCES WHERE UPPER(SEQ_NAME) LIKE '"
								+ SEQ_PREFIX.toUpperCase( Locale.ROOT ) + "%'" ) ) {
			while ( rs.next() ) {
				names.add( rs.getString( 1 ) );
			}
		}
		return names;
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			ignore( st, "DROP TABLE IF EXISTS " + TABLE );
			for ( String seq : listMatchingSequencesSafe() ) {
				ignore( st, "DROP SEQUENCE IF EXISTS " + seq );
			}
			// Common Hibernate AUTO naming fallbacks
			ignore( st, "DROP SEQUENCE IF EXISTS " + TABLE + "_SEQ" );
			ignore( st, "DROP SEQUENCE IF EXISTS I004P001AutoEntity_SEQ" );
			ignore( st, "DROP SEQUENCE IF EXISTS hibernate_sequence" );
		}
		catch ( Exception ignored ) {
		}
	}

	private static List<String> listMatchingSequencesSafe() {
		try {
			return listMatchingSequences();
		}
		catch ( Exception e ) {
			return List.of();
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
		while ( cur.getCause() != null && cur.getCause() != cur ) {
			cur = cur.getCause();
		}
		String msg = cur.getMessage();
		return msg != null ? msg : String.valueOf( cur );
	}
}
