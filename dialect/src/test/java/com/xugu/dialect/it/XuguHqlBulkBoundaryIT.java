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
import org.hibernate.query.Query;
import org.hibernate.query.SyntaxException;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I003P005BulkDoctor;
import com.xugu.dialect.it.entities.I003P005BulkEngineer;
import com.xugu.dialect.it.entities.I003P005BulkPerson;
import com.xugu.dialect.it.entities.I010P013StAnimal;
import com.xugu.dialect.it.entities.I010P013StCat;
import com.xugu.dialect.it.entities.I010P013StDog;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT for XP-003 / P-013: SINGLE_TABLE bulk success; JOINED cross-ref /
 * success; order-by / limit mutation reject boundaries.
 *
 * <p>JOINED success path also covered by {@link XuguBulkMutationIT}; this class
 * reuses {@code I003P005Bulk*} for at least one live JOINED bulk assertion.
 */
class XuguHqlBulkBoundaryIT {

	private static final String[] ST_TABLES = {
			"HIB_I010_P013_ST_ANIMAL"
	};

	private static final String[] JOINED_TABLES = {
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
	void singleTableBulkUpdateAndDeleteSucceed() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup( ST_TABLES );

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildStMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			seedAnimals( sf );

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				int updated = session.createMutationQuery(
						"update I010P013StDog set name = :name where barkSound = :sound" )
						.setParameter( "name", "Rex2" )
						.setParameter( "sound", "woof" )
						.executeUpdate();
				assertEquals( 1, updated, "SINGLE_TABLE bulk update row count" );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				int deleted = session.createMutationQuery(
						"delete from I010P013StCat where name = :name" )
						.setParameter( "name", "Mia" )
						.executeUpdate();
				assertEquals( 1, deleted, "SINGLE_TABLE bulk delete row count" );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I010P013StDog dog = session.createQuery(
						"from I010P013StDog d where d.name = 'Rex2'", I010P013StDog.class )
						.getSingleResult();
				assertNotNull( dog );
				Long cats = session.createQuery(
						"select count(c) from I010P013StCat c", Long.class )
						.getSingleResult();
				assertEquals( 0L, cats );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "SINGLE_TABLE bulk IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup( ST_TABLES );
		}
	}

	/**
	 * JOINED bulk success — aligns with {@link XuguBulkMutationIT#bulkUpdateOnJoinedInheritanceSucceeds()}.
	 */
	@Test
	void joinedBulkUpdateSucceeds_crossRefI003P005() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup( JOINED_TABLES );

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildJoinedMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				for ( int i = 0; i < 4; i++ ) {
					I003P005BulkDoctor doctor = new I003P005BulkDoctor();
					doctor.setName( "p013-doc-" + i );
					doctor.setEmployed( i % 2 == 0 );
					session.persist( doctor );
				}
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				int updated = session.createMutationQuery(
						"update I003P005BulkPerson set name = :name where employed = :employed" )
						.setParameter( "name", "p013-joined-updated" )
						.setParameter( "employed", true )
						.executeUpdate();
				assertEquals( 2, updated, "JOINED bulk update (cross-ref XuguBulkMutationIT)" );
				session.getTransaction().commit();
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "JOINED bulk IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup( JOINED_TABLES );
		}
	}

	@Test
	void mutationWithOrderByOrLimitIsRejected() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup( ST_TABLES );

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = buildStMetadata( registry );
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();
			seedAnimals( sf );

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();

				RuntimeException orderByEx = assertThrows(
						RuntimeException.class,
						() -> session.createMutationQuery(
								"update I010P013StDog set name = 'x' order by id" ) );
				assertInstanceOf( SyntaxException.class, rootCause( orderByEx ),
						() -> "order by reject: " + describe( orderByEx ) );

				RuntimeException limitHqlEx = assertThrows(
						RuntimeException.class,
						() -> session.createMutationQuery(
								"delete from I010P013StCat where name is not null limit 1" ) );
				assertInstanceOf( SyntaxException.class, rootCause( limitHqlEx ),
						() -> "limit HQL reject: " + describe( limitHqlEx ) );

				// Soft via Query API: MutationQuery has no setMaxResults; createQuery ignores it.
				@SuppressWarnings("rawtypes")
				Query soft = session.createQuery(
						"update I010P013StAnimal set name = :name where name is not null" );
				soft.setParameter( "name", "all-touched" );
				soft.setMaxResults( 1 );
				int updated = soft.executeUpdate();
				assertEquals( 2, updated,
						"Query.setMaxResults on mutation HQL is soft no-op; both Dog+Cat must update" );

				session.getTransaction().commit();
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Boundary reject IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup( ST_TABLES );
		}
	}

	private static void seedAnimals(SessionFactory sf) {
		try ( Session session = sf.openSession() ) {
			session.beginTransaction();
			I010P013StDog dog = new I010P013StDog();
			dog.setName( "Rex" );
			dog.setBarkSound( "woof" );
			session.persist( dog );
			I010P013StCat cat = new I010P013StCat();
			cat.setName( "Mia" );
			cat.setWhiskerLength( 3 );
			session.persist( cat );
			session.getTransaction().commit();
		}
	}

	private static Metadata buildStMetadata(StandardServiceRegistry registry) {
		return new MetadataSources( registry )
				.addAnnotatedClass( I010P013StAnimal.class )
				.addAnnotatedClass( I010P013StDog.class )
				.addAnnotatedClass( I010P013StCat.class )
				.buildMetadata();
	}

	private static Metadata buildJoinedMetadata(StandardServiceRegistry registry) {
		return new MetadataSources( registry )
				.addAnnotatedClass( I003P005BulkPerson.class )
				.addAnnotatedClass( I003P005BulkDoctor.class )
				.addAnnotatedClass( I003P005BulkEngineer.class )
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

	private static void cleanup(String[] tables) {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			for ( String table : tables ) {
				st.execute( "DROP TABLE IF EXISTS " + table );
			}
		}
		catch ( Exception ignored ) {
		}
	}

	private static Throwable rootCause(Throwable t) {
		Throwable cur = t;
		while ( cur.getCause() != null && cur.getCause() != cur ) {
			cur = cur.getCause();
		}
		return cur;
	}

	private static String describe(Throwable t) {
		StringBuilder sb = new StringBuilder();
		Throwable cur = t;
		while ( cur != null ) {
			if ( sb.length() > 0 ) {
				sb.append( " <- " );
			}
			sb.append( cur.getClass().getName() ).append( ": " ).append( cur.getMessage() );
			Throwable next = cur.getCause();
			if ( next == cur ) {
				break;
			}
			cur = next;
		}
		return sb.toString();
	}
}
