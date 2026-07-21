package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
import com.xugu.dialect.it.entities.I010P014TxEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * XP-005: ORM explicit Session transaction atomicity — multi-persist commit all / rollback none.
 * Uses {@link Session#beginTransaction()}; does not depend on Spring.
 */
class XuguExplicitTxAtomicityIT {

	private static final String TABLE = "HIB_I010_P014_TX";

	@Test
	void multiPersistCommitMakesBothRowsVisible_XP_005() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				Transaction tx = session.beginTransaction();
				session.persist( new I010P014TxEntity( 1, "a" ) );
				session.persist( new I010P014TxEntity( 2, "b" ) );
				tx.commit();
			}

			try ( Session session = sf.openSession() ) {
				assertNotNull( session.find( I010P014TxEntity.class, 1 ) );
				assertNotNull( session.find( I010P014TxEntity.class, 2 ) );
				Long count = session.createQuery(
						"select count(e) from I010P014TxEntity e", Long.class )
						.getSingleResult();
				assertEquals( 2L, count, "XP-005: commit must make both persists visible" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "XP-005 commit atomicity IT failed: " + e.getMessage(), e );
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
	void multiPersistRollbackLeavesZeroRows_XP_005() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				Transaction tx = session.beginTransaction();
				session.persist( new I010P014TxEntity( 10, "x" ) );
				session.persist( new I010P014TxEntity( 20, "y" ) );
				tx.rollback();
			}

			try ( Session session = sf.openSession() ) {
				Long count = session.createQuery(
						"select count(e) from I010P014TxEntity e", Long.class )
						.getSingleResult();
				assertEquals( 0L, count, "XP-005: rollback must leave zero rows" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "XP-005 rollback atomicity IT failed: " + e.getMessage(), e );
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
				.addAnnotatedClass( I010P014TxEntity.class )
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
