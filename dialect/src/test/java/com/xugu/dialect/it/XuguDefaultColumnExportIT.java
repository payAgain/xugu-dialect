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
import com.xugu.dialect.it.entities.P002DefaultColumnEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated SchemaExport + live DB assert for A-DDL-005: DEFAULT column clause in CREATE DDL.
 */
class XuguDefaultColumnExportIT {

	private static final String TABLE = "HIB_P002_DEFAULT_PROBE";

	@Test
	void schemaExportEmitsDefaultColumnAndAppliesOnDb_A_DDL_005() {
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
				.build();

		try {
			scriptFile = Files.createTempFile( "hib-p003-default-", ".sql" );
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P002DefaultColumnEntity.class )
					.buildMetadata();

			Map<String, Object> create = new HashMap<>();
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE_ONLY );
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, Action.CREATE_ONLY );
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_CREATE_TARGET,
					scriptFile.toAbsolutePath().toString() );
			SchemaManagementToolCoordinator.process( metadata, registry, create, action -> {
			} );

			String ddl = Files.readString( scriptFile, StandardCharsets.UTF_8 );
			String lower = ddl.toLowerCase( Locale.ROOT );
			assertTrue( lower.contains( "create table" ), "expected CREATE TABLE script: " + ddl );
			assertTrue( lower.contains( "default" ), "expected DEFAULT clause in script: " + ddl );
			assertTrue( lower.contains( "active" ), "expected default literal in script: " + ddl );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "INSERT INTO " + TABLE + " (id) VALUES (1)" );
				try ( ResultSet rs = st.executeQuery( "SELECT status FROM " + TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next() );
					assertEquals( "active", rs.getString( 1 ) );
				}
			}

			Map<String, Object> drop = new HashMap<>();
			drop.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.DROP );
			SchemaManagementToolCoordinator.process( metadata, registry, drop, action -> {
			} );
		}
		catch ( Exception e ) {
			fail( "Default column SchemaExport IT failed: " + e.getMessage(), e );
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
}
