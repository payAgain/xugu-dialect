package com.xugu.dialect.it;

import java.sql.Connection;
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
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I003P002UniqueEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated ORM entrypoint IT for C-EXC-001: Session persist of duplicate unique value
 * must surface {@link ConstraintViolationException} (UNIQUE).
 *
 * <p>C-EXC-002 field-name extraction is covered offline in
 * {@link com.xugu.dialect.XuguExceptionConversionTest} (E16005 template).
 */
class XuguExceptionMappingIT {

	private static final String TABLE = "HIB_I003_P002_UNQ";

	@Test
	void sessionUniqueViolationMapsToConstraintViolationException() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		assertNotNull( new XuguDialect().buildSQLExceptionConversionDelegate() );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I003P002UniqueEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I003P002UniqueEntity( "DUP", "first" ) );
				session.getTransaction().commit();
			}

			ConstraintViolationException cve = null;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I003P002UniqueEntity( "DUP", "second" ) );
				session.getTransaction().commit();
				fail( "expected unique constraint violation on second persist" );
			}
			catch ( ConstraintViolationException ex ) {
				cve = ex;
			}
			catch ( RuntimeException ex ) {
				Throwable t = ex;
				while ( t != null ) {
					if ( t instanceof ConstraintViolationException found ) {
						cve = found;
						break;
					}
					t = t.getCause();
				}
				if ( cve == null ) {
					fail( "expected ConstraintViolationException in cause chain, got: " + ex, ex );
				}
			}

			assertNotNull( cve );
			assertEquals( ConstraintKind.UNIQUE, cve.getKind(), "unique violation must map to UNIQUE kind" );

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "exception-mapping IT failed: " + e.getMessage(), e );
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
