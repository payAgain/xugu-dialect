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
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I010P012VersionedProduct;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import jakarta.persistence.OptimisticLockException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * XP-001 gated IT: {@code @Version} stale concurrent write → OptimisticLockException
 * (ref xuguefcore OptimisticConcurrencyTests).
 */
class XuguOptimisticConcurrencyIT {

	private static final String TABLE = "HIB_I010_P012_PRODUCT";

	@Test
	void staleVersionWriteThrowsOptimisticLockException() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P012VersionedProduct( 1, "Widget" ) );
				session.getTransaction().commit();
			}

			I010P012VersionedProduct inSession1;
			I010P012VersionedProduct inSession2;
			try ( Session session1 = sf.openSession(); Session session2 = sf.openSession() ) {
				session1.beginTransaction();
				session2.beginTransaction();

				inSession1 = session1.find( I010P012VersionedProduct.class, 1 );
				inSession2 = session2.find( I010P012VersionedProduct.class, 1 );
				assertNotNull( inSession1 );
				assertNotNull( inSession2 );
				Long versionBefore = inSession1.getVersion();
				assertNotNull( versionBefore );

				inSession1.setName( "Widget A" );
				session1.getTransaction().commit();

				I010P012VersionedProduct afterCommit;
				try ( Session verify = sf.openSession() ) {
					afterCommit = verify.find( I010P012VersionedProduct.class, 1 );
				}
				assertNotNull( afterCommit );
				assertEquals( "Widget A", afterCommit.getName() );
				assertTrue( afterCommit.getVersion() > versionBefore,
						"version must increment after successful commit" );

				inSession2.setName( "Widget B" );
				boolean threwOptimistic = false;
				try {
					session2.getTransaction().commit();
				}
				catch ( OptimisticLockException e ) {
					// covers jakarta.persistence.OptimisticLockException and Hibernate subclass
					threwOptimistic = true;
					if ( session2.getTransaction().isActive() ) {
						session2.getTransaction().rollback();
					}
				}
				assertTrue( threwOptimistic,
						"stale @Version write must throw OptimisticLockException (JPA or Hibernate)" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Optimistic concurrency IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	@Test
	void successfulVersionedWriteIsReadable() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P012VersionedProduct( 2, "Alpha" ) );
				session.getTransaction().commit();
			}

			Long versionAfterFirstWrite;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				I010P012VersionedProduct product = session.find( I010P012VersionedProduct.class, 2 );
				assertNotNull( product );
				assertEquals( "Alpha", product.getName() );
				versionAfterFirstWrite = product.getVersion();
				assertNotNull( versionAfterFirstWrite );
				product.setName( "Beta" );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I010P012VersionedProduct loaded = session.find( I010P012VersionedProduct.class, 2 );
				assertNotNull( loaded );
				assertEquals( "Beta", loaded.getName() );
				assertNotNull( loaded.getVersion() );
				assertTrue( loaded.getVersion() > versionAfterFirstWrite,
						"version must increment on successful update" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Versioned write IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	private static Metadata buildMetadata(StandardServiceRegistry registry) {
		return new MetadataSources( registry )
				.addAnnotatedClass( I010P012VersionedProduct.class )
				.buildMetadata();
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
