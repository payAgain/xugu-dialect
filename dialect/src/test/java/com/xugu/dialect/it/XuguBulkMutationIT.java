package com.xugu.dialect.it;

import java.sql.Connection;
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
import com.xugu.dialect.it.entities.I003P005BulkDoctor;
import com.xugu.dialect.it.entities.I003P005BulkEngineer;
import com.xugu.dialect.it.entities.I003P005BulkPerson;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ORM entrypoint IT for C-BULK-001 / C-BULK-003: HQL bulk update/delete on JOINED
 * inheritance triggers local-temp-table mutation fallback on live XuguDB.
 */
class XuguBulkMutationIT {

	private static final int ENTITY_COUNT = 6;

	private static final String[] TABLES = {
			"HIB_I003_P005_ENGINEER",
			"HIB_I003_P005_DOCTOR",
			"HIB_I003_P005_PERSON"
	};

	@Test
	void dialectExposesLocalTempBulkStrategyFlags() {
		XuguDialect dialect = new XuguDialect();
		assertFalse( dialect.supportsSubqueryOnMutatingTable() );
		assertTrue(
				dialect.getTemporaryTableCreateCommand().toLowerCase( Locale.ROOT )
						.contains( "local temporary table" ) );
	}

	@Test
	void bulkUpdateOnJoinedInheritanceSucceeds() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			seedDoctorsOnly( sf );

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				int updated = session.createMutationQuery(
						"update I003P005BulkPerson set name = :name where employed = :employed" )
						.setParameter( "name", "bulk-updated" )
						.setParameter( "employed", true )
						.executeUpdate();
				assertEquals( ENTITY_COUNT / 2, updated, "C-BULK-001: bulk update row count" );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I003P005BulkDoctor sample = session.createQuery(
						"from I003P005BulkDoctor d where d.name = 'bulk-updated'", I003P005BulkDoctor.class )
						.setMaxResults( 1 )
						.getSingleResult();
				assertNotNull( sample );
				assertEquals( "bulk-updated", sample.getName() );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Bulk update IT failed: " + e.getMessage(), e );
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
	void bulkDeleteOnJoinedInheritanceSucceeds() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			seedDoctorsOnly( sf );

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				int deleted = session.createMutationQuery(
						"delete from I003P005BulkDoctor where employed = :employed" )
						.setParameter( "employed", false )
						.executeUpdate();
				assertEquals( ENTITY_COUNT / 2, deleted, "C-BULK-001: bulk delete row count" );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				Long remaining = session.createQuery(
						"select count(d) from I003P005BulkDoctor d", Long.class )
						.getSingleResult();
				assertEquals( ENTITY_COUNT / 2, remaining );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Bulk delete IT failed: " + e.getMessage(), e );
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
				.addAnnotatedClass( I003P005BulkPerson.class )
				.addAnnotatedClass( I003P005BulkDoctor.class )
				.addAnnotatedClass( I003P005BulkEngineer.class )
				.buildMetadata();
	}

	@Test
	void bulkInsertOnJoinedInheritanceWithIdentitySucceeds_C_BULK_002() {
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
				int inserted = session.createMutationQuery(
						"insert into I003P005BulkDoctor (name, employed) values ('bulk-insert-doctor', true)" )
						.executeUpdate();
				assertEquals( 1, inserted, "C-BULK-002: bulk insert row count" );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				Long count = session.createQuery(
						"select count(d) from I003P005BulkDoctor d where d.name = 'bulk-insert-doctor'",
						Long.class )
						.getSingleResult();
				assertEquals( 1L, count, "C-BULK-002: bulk-inserted doctor visible after commit" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Bulk insert IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	private static void seedDoctorsOnly(SessionFactory sf) {
		try ( Session session = sf.openSession() ) {
			session.beginTransaction();
			for ( int i = 0; i < ENTITY_COUNT; i++ ) {
				I003P005BulkDoctor doctor = new I003P005BulkDoctor();
				doctor.setName( "doctor-" + i );
				doctor.setEmployed( i % 2 == 0 );
				session.persist( doctor );
			}
			session.getTransaction().commit();
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
			for ( String table : TABLES ) {
				st.execute( "DROP TABLE IF EXISTS " + table );
			}
		}
		catch ( Exception ignored ) {
		}
	}
}
