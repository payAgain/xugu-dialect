package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
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
import com.xugu.dialect.it.entities.P003DdlProbeEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Hibernate SchemaExport / metadata DDL against real XuguDB (gated).
 * Covers CREATE/DROP with PRIMARY KEY + NOT NULL (A-DDL-001/003/004/006)
 * and ALTER ADD COLUMN smoke (A-DDL-002).
 */
class XuguDdlIT {

	private static final String TABLE = "HIB_P003_DDL_PROBE";

	@Test
	void schemaExportCreateDropWithPkAndNotNull() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection probe = XuguTestConnection.open(); Statement st = probe.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
		}
		catch ( Exception e ) {
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();

		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P003DdlProbeEntity.class )
					.buildMetadata();

			Map<String, Object> create = new HashMap<>();
			// CREATE_ONLY avoids drop-before-create noise when table is absent (A-DDL-007 IF NOT EXISTS via C-DDL-001)
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE_ONLY );
			SchemaManagementToolCoordinator.process( metadata, registry, create, action -> {
			} );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				assertTrue( tableExists( st, TABLE ), "table should exist after schema create" );
				st.execute( "INSERT INTO " + TABLE + " (id, name) VALUES (1, 'n')" );
				boolean nullRejected = false;
				try {
					st.execute( "INSERT INTO " + TABLE + " (id, name) VALUES (2, NULL)" );
				}
				catch ( Exception e ) {
					nullRejected = true;
				}
				assertTrue( nullRejected, "NOT NULL on name should reject null" );

				// ALTER ADD COLUMN path (A-DDL-002)
				st.execute( "ALTER TABLE " + TABLE + " ADD COLUMN extra_col VARCHAR(32)" );
				st.execute( "UPDATE " + TABLE + " SET extra_col = 'x' WHERE id = 1" );
				try ( ResultSet rs = st.executeQuery( "SELECT extra_col FROM " + TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next() );
					assertTrue( "x".equalsIgnoreCase( rs.getString( 1 ) ) || "x".equals( rs.getString( 1 ) ) );
				}
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
			fail( "DDL IT failed: " + e.getMessage(), e );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( registry );
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
			}
			catch ( Exception ignored ) {
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
			// fallback: try select
		}
		try ( ResultSet rs = st.executeQuery( "SELECT 1 FROM " + table + " WHERE 1=0" ) ) {
			return true;
		}
		catch ( Exception e ) {
			return false;
		}
	}
}
