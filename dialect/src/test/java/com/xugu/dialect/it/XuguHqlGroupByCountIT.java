package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
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
import com.xugu.dialect.it.entities.I010P012Customer;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * XP-002 gated IT: HQL scalar {@code count} + {@code group by} count projection materialize
 * (ref xuguefcore RuntimeGapBaselineTests Count·GroupBy).
 */
class XuguHqlGroupByCountIT {

	private static final String TABLE = "HIB_I010_P012_CUSTOMER";

	@Test
	void scalarCountMaterializesAsNumber() {
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
				session.persist( new I010P012Customer( 1, "Alpha", "Chengdu" ) );
				session.persist( new I010P012Customer( 2, "Beta", "Beijing" ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				Number count = session.createQuery(
						"select count(c) from I010P012Customer c", Number.class )
						.getSingleResult();
				assertNotNull( count );
				assertInstanceOf( Number.class, count );
				assertEquals( 2, count.intValue(), "scalar count must materialize as 2" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "HQL scalar count IT failed: " + e.getMessage(), e );
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
	void groupByCityCountMaterializes() {
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
				session.persist( new I010P012Customer( 1, "Alpha", "Chengdu" ) );
				session.persist( new I010P012Customer( 2, "Beta", "Chengdu" ) );
				session.persist( new I010P012Customer( 3, "Gamma", "Beijing" ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				List<Object[]> groups = session.createQuery(
						"select c.city, count(c) from I010P012Customer c group by c.city order by c.city",
						Object[].class )
						.getResultList();
				assertEquals( 2, groups.size(), "expect Beijing + Chengdu groups" );

				Object[] beijing = groups.get( 0 );
				assertEquals( "Beijing", beijing[0] );
				assertInstanceOf( Number.class, beijing[1] );
				assertEquals( 1, ( (Number) beijing[1] ).intValue() );

				Object[] chengdu = groups.get( 1 );
				assertEquals( "Chengdu", chengdu[0] );
				assertInstanceOf( Number.class, chengdu[1] );
				assertEquals( 2, ( (Number) chengdu[1] ).intValue() );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "HQL group-by count IT failed: " + e.getMessage(), e );
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
				.addAnnotatedClass( I010P012Customer.class )
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
