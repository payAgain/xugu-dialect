package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
import com.xugu.dialect.it.entities.P002SequenceEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT (P-002): sequence metadata from {@code ALL_SEQUENCES} so hbm2ddl validate
 * sees existing sequences; missing sequence still fails diagnostically.
 */
class XuguSchemaValidateIT {

	private static final String SEQ_TABLE = "HIB_P002_SEQ_ENT";
	private static final String SEQ_NAME = "HIB_P002_SEQ_GEN";

	@Test
	void schemaValidateSucceedsWhenSequenceExists() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P002SequenceEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );

			assertSequenceVisibleInAllSequences();

			export( metadata, registry, Action.VALIDATE );
		}
		catch ( Exception e ) {
			fail( "schema validate should succeed when sequence exists: " + e.getMessage(), e );
		}
		finally {
			try {
				Metadata metadata = new MetadataSources( registry )
						.addAnnotatedClass( P002SequenceEntity.class )
						.buildMetadata();
				export( metadata, registry, Action.DROP );
			}
			catch ( Exception ignored ) {
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	@Test
	void schemaValidateFailsWhenSequenceMissing() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry createRegistry = buildRegistry();
		try {
			Metadata createMetadata = new MetadataSources( createRegistry )
					.addAnnotatedClass( P002SequenceEntity.class )
					.buildMetadata();
			export( createMetadata, createRegistry, Action.CREATE_ONLY );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "DROP SEQUENCE IF EXISTS " + SEQ_NAME );
			}

			boolean failed = false;
			String message = "";
			try {
				export( createMetadata, createRegistry, Action.VALIDATE );
			}
			catch ( Exception e ) {
				failed = true;
				message = rootMessage( e ).toLowerCase( Locale.ROOT );
			}

			assertTrue( failed, "validate must fail when mapped sequence was dropped" );
			assertTrue(
					message.contains( "missing sequence" )
							|| message.contains( "sequence" ) && message.contains( "missing" )
							|| message.contains( SEQ_NAME.toLowerCase( Locale.ROOT ) ),
					"expected diagnostic mentioning missing sequence, got: " + message );
		}
		catch ( Exception e ) {
			fail( "missing-sequence boundary IT failed unexpectedly: " + e.getMessage(), e );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( createRegistry );
			cleanup();
		}
	}

	private static void assertSequenceVisibleInAllSequences() throws Exception {
		String query = new XuguDialect().getQuerySequencesString();
		assertTrue( query != null && query.toLowerCase( Locale.ROOT ).contains( "all_sequences" ) );

		try ( Connection c = XuguTestConnection.open();
				PreparedStatement ps = c.prepareStatement(
						"select seq_name from all_sequences where seq_name = ?" ) ) {
			ps.setString( 1, SEQ_NAME );
			try ( ResultSet rs = ps.executeQuery() ) {
				assertTrue( rs.next(), "created sequence must appear in all_sequences" );
			}
		}
	}

	private static StandardServiceRegistry buildRegistry() {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, a -> {
		} );
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			ignore( st, "DROP TABLE IF EXISTS " + SEQ_TABLE );
			ignore( st, "DROP SEQUENCE IF EXISTS " + SEQ_NAME );
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
		StringBuilder sb = new StringBuilder();
		Throwable cur = t;
		while ( cur != null ) {
			if ( cur.getMessage() != null ) {
				if ( sb.length() > 0 ) {
					sb.append( " | " );
				}
				sb.append( cur.getMessage() );
			}
			cur = cur.getCause();
		}
		return sb.toString();
	}
}
