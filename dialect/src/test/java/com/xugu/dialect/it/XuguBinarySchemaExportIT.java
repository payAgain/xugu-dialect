package com.xugu.dialect.it;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.xugu.dialect.it.entities.P003BinaryEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * SchemaExport of a {@code byte[]}/{@code VARBINARY} attribute must emit bare {@code BINARY}
 * (XuGu docs: no length param). Gated by {@code -Dxugu.run.integration=true}.
 */
class XuguBinarySchemaExportIT {

	private static final String TABLE = "HIB_P003_BINARY_PROBE";
	private static final Pattern BARE_BINARY = Pattern.compile( "(?i)\\bbinary\\b" );
	private static final Pattern BINARY_WITH_LENGTH = Pattern.compile( "(?i)\\bbinary\\s*\\(" );

	@Test
	void schemaExportEmitsBareBinaryAndCreatesOnDb() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection probe = XuguTestConnection.open(); Statement st = probe.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
		}
		catch ( Exception e ) {
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}

		Path scriptFile = null;
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();

		try {
			scriptFile = Files.createTempFile( "hib-p003-binary-", ".sql" );
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P003BinaryEntity.class )
					.buildMetadata();

			Map<String, Object> create = new HashMap<>();
			// CREATE_ONLY for both DB and script so drop-script target is not required
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE_ONLY );
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, Action.CREATE_ONLY );
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_CREATE_TARGET,
					scriptFile.toAbsolutePath().toString() );
			SchemaManagementToolCoordinator.process( metadata, registry, create, action -> {
			} );

			String ddl = Files.readString( scriptFile, StandardCharsets.UTF_8 );
			String lower = ddl.toLowerCase( Locale.ROOT );
			assertTrue( BARE_BINARY.matcher( ddl ).find(),
					"SchemaExport DDL must contain bare binary; got: " + ddl );
			assertFalse( BINARY_WITH_LENGTH.matcher( ddl ).find(),
					"SchemaExport DDL must not use binary(n); got: " + ddl );
			assertTrue( lower.contains( "create table" ), "expected CREATE TABLE in script: " + ddl );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				assertTrue( tableExists( st, TABLE ),
						"table should exist after SchemaExport CREATE of binary column" );
				st.execute( "INSERT INTO " + TABLE + " (id, payload) VALUES (1, X'0102')" );
			}

			Map<String, Object> drop = new HashMap<>();
			drop.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.DROP );
			SchemaManagementToolCoordinator.process( metadata, registry, drop, action -> {
			} );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				assertFalse( tableExists( st, TABLE ), "table should be gone after schema drop" );
			}
		}
		catch ( Exception e ) {
			fail( "Binary SchemaExport IT failed: " + e.getMessage(), e );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( registry );
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
			}
			catch ( Exception ignored ) {
			}
			if ( scriptFile != null ) {
				try {
					Files.deleteIfExists( scriptFile );
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}

	private static boolean tableExists(Statement st, String table) throws Exception {
		try ( ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = '" + table.toUpperCase() + "'" ) ) {
			if ( rs.next() && rs.getInt( 1 ) > 0 ) {
				return true;
			}
		}
		catch ( Exception ignored ) {
		}
		try ( ResultSet rs = st.executeQuery( "SELECT 1 FROM " + table + " WHERE 1=0" ) ) {
			return true;
		}
		catch ( Exception e ) {
			return false;
		}
	}
}
