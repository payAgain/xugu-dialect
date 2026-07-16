package com.xugu.dialect.it;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I003P006TypeEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ORM/schema entrypoint IT for I-003 / P-006 Type/DDL details.
 */
class XuguTypeDdlDetailsIT {

	private static final String TYPE_TABLE = "HIB_I003_P006_TYPE";
	private static final String ALTER_TABLE = "HIB_I003_P006_ALTER";
	private static final String CATALOG = "HIB_I003_P006_CAT";

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void typeDdlDetailsOnLiveDb() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		assertTrue( dialect.supportsIfExistsBeforeTableName() );
		assertTrue( dialect.supportsAlterColumnType() );
		assertTrue( dialect.canCreateCatalog() );
		assertEquals( "select sys_guid()", dialect.getSelectGUIDString() );

		cleanup();

		Path scriptFile = null;
		CapturingInspector inspector = new CapturingInspector();
		StandardServiceRegistry registry = buildRegistry( inspector );
		SessionFactory sf = null;
		try {
			scriptFile = Files.createTempFile( "hib-i003-p006-", ".sql" );
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I003P006TypeEntity.class )
					.buildMetadata();

			// --- C-DDL-001: schema export CREATE emits IF NOT EXISTS ---
			Map<String, Object> create = new HashMap<>();
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.CREATE_ONLY );
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, Action.CREATE_ONLY );
			create.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_CREATE_TARGET,
					scriptFile.toAbsolutePath().toString() );
			SchemaManagementToolCoordinator.process( metadata, registry, create, action -> {
			} );

			String ddl = Files.readString( scriptFile, StandardCharsets.UTF_8 ).toLowerCase( Locale.ROOT );
			assertTrue( ddl.contains( "if not exists" ), "CREATE script must emit IF NOT EXISTS: " + ddl );
			assertTrue( ddl.contains( "create table" ), ddl );

			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				assertTrue( tableExists( st, TYPE_TABLE ), "table should exist after CREATE IF NOT EXISTS export" );
			}

			sf = metadata.buildSessionFactory();

			// --- C-DDL-003: HQL datetime / to_char / current_timestamp on live Session ---
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I003P006TypeEntity( 1, "a", LocalDateTime.of( 2024, 1, 15, 10, 30, 0 ) ) );
				session.persist( new I003P006TypeEntity( 2, "b", LocalDateTime.of( 2025, 6, 20, 15, 16, 25 ) ) );
				session.getTransaction().commit();
			}

			inspector.clear();
			try ( Session session = sf.openSession() ) {
				List<Integer> ids = session.createQuery(
						"select e.id from I003P006TypeEntity e "
								+ "where e.createdAt > local datetime 2024-06-01 00:00:00 "
								+ "order by e.id",
						Integer.class )
						.getResultList();
				assertEquals( List.of( 2 ), ids );

				// Assert XuGu-style timestamp literal reached SQL (C-DDL-003)
				boolean sawTimestampLiteral = inspector.sqls.stream()
						.map( s -> s.toLowerCase( Locale.ROOT ) )
						.anyMatch( s -> s.contains( "timestamp '" ) || s.contains( "timestamp'" ) );
				assertTrue( sawTimestampLiteral,
						"expected timestamp '…' literal in SQL: " + inspector.sqls );

				String formatted = session.createQuery(
						"select cast(to_char(e.createdAt, 'YYYY-MM-DD') as string) "
								+ "from I003P006TypeEntity e where e.id = 2",
						String.class )
						.getSingleResult();
				assertNotNull( formatted );
				assertTrue( formatted.startsWith( "2025-06-20" ), "to_char result: " + formatted );

				Object now = session.createQuery(
						"select current_timestamp from I003P006TypeEntity e where e.id = 1",
						Object.class )
						.getSingleResult();
				assertNotNull( now );
			}

			// --- C-GUID-001: dialect GUID select via Session native query ---
			try ( Session session = sf.openSession() ) {
				Object guid = session.createNativeQuery( dialect.getSelectGUIDString(), Object.class )
						.getSingleResult();
				assertNotNull( guid, "sys_guid() must return a value" );
				String asText = String.valueOf( guid ).replace( "-", "" );
				assertTrue( asText.length() >= 16, "unexpected guid: " + guid );
			}

			export( metadata, registry, Action.DROP );

			// --- C-DDL-002: ALTER column type on live table (integer→varchar) ---
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + ALTER_TABLE );
				// nullable + empty before type change — XuGu rejects alter when NOT NULL blocks conversion
				st.execute( "CREATE TABLE " + ALTER_TABLE + " (id integer primary key, payload integer)" );
				String alterSql = dialect.getAlterTableString( ALTER_TABLE ) + " "
						+ dialect.getAlterColumnTypeString( "payload", "varchar(32)", "varchar(32)" );
				assertTrue( alterSql.toLowerCase( Locale.ROOT ).contains( "alter column" ), alterSql );
				st.execute( alterSql );

				st.execute( "INSERT INTO " + ALTER_TABLE + " (id, payload) VALUES (1, 'forty-two')" );
				try ( ResultSet rs = st.executeQuery( "SELECT payload FROM " + ALTER_TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next() );
					assertEquals( "forty-two", rs.getString( 1 ) );
					ResultSetMetaData md = rs.getMetaData();
					String typeName = md.getColumnTypeName( 1 ).toLowerCase( Locale.ROOT );
					assertTrue(
							typeName.contains( "char" ) || typeName.contains( "varchar" ),
							"expected varchar-like type after alter, got: " + typeName );
				}
			}

			// --- C-CAT-001: create/drop catalog (database), unique name, cleanup ---
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				try {
					st.execute( dialect.getDropCatalogCommand( CATALOG )[0] );
				}
				catch ( Exception ignored ) {
					// catalog may not exist yet
				}
				st.execute( dialect.getCreateCatalogCommand( CATALOG )[0] );
				assertTrue( catalogExists( st, CATALOG ), "catalog should exist after CREATE DATABASE" );
				st.execute( dialect.getDropCatalogCommand( CATALOG )[0] );
				assertFalse( catalogExists( st, CATALOG ), "catalog should be gone after DROP DATABASE" );
			}
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Type/DDL details IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
			if ( scriptFile != null ) {
				try {
					Files.deleteIfExists( scriptFile );
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}

	private static StandardServiceRegistry buildRegistry(StatementInspector inspector) {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.STATEMENT_INSPECTOR, inspector )
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
			st.execute( "DROP TABLE IF EXISTS " + TYPE_TABLE );
			st.execute( "DROP TABLE IF EXISTS " + ALTER_TABLE );
			try {
				st.execute( "DROP DATABASE IF EXISTS " + CATALOG );
			}
			catch ( Exception e ) {
				try {
					st.execute( "DROP DATABASE " + CATALOG );
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception ignored ) {
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

	private static boolean catalogExists(Statement st, String catalog) throws Exception {
		try ( ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) FROM ALL_DATABASES WHERE DB_NAME = '" + catalog.toUpperCase() + "'" ) ) {
			if ( rs.next() ) {
				return rs.getInt( 1 ) > 0;
			}
		}
		catch ( Exception e ) {
			// fallback: try USE / reconnect probe is heavy — treat select failure as absent
		}
		try ( ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) FROM USER_DATABASES WHERE DB_NAME = '" + catalog.toUpperCase() + "'" ) ) {
			if ( rs.next() ) {
				return rs.getInt( 1 ) > 0;
			}
		}
		catch ( Exception ignored ) {
		}
		return false;
	}

	private static final class CapturingInspector implements StatementInspector {
		private final CopyOnWriteArrayList<String> sqls = new CopyOnWriteArrayList<>();

		@Override
		public String inspect(String sql) {
			sqls.add( sql );
			return sql;
		}

		void clear() {
			sqls.clear();
		}
	}
}
