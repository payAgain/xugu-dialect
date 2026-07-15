package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
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
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.XuguDialectResolver;
import com.xugu.dialect.it.entities.P008ProbeEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: explicit dialect + SPI auto-resolve against live XuguDB (P-008).
 * Connection uses {@code compatiblemode=NONE} (A-XCUT-003).
 */
class XuguDialectResolverIT {

	private static final String TABLE = "HIB_P008_PROBE";

	@Test
	void liveMetadata_productAndDriverContainXugu() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		try ( Connection c = XuguTestConnection.open() ) {
			DatabaseMetaData md = c.getMetaData();
			String product = md.getDatabaseProductName();
			String driver = md.getDriverName();
			System.out.println( "P-008 live productName=[" + product + "] driverName=[" + driver + "]"
					+ " version=[" + md.getDatabaseProductVersion() + "]"
					+ " major=" + md.getDatabaseMajorVersion()
					+ " minor=" + md.getDatabaseMinorVersion() );
			assertTrue(
					XuguDialectResolver.containsXuguToken( product )
							|| XuguDialectResolver.containsXuguToken( driver ),
					"expected Xugu token in product or driver; product=" + product + " driver=" + driver );
			assertEquals( "XuguDB", product, "document match rule against observed product name" );
		}
		catch ( Exception e ) {
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}
	}

	@Test
	void explicitDialect_sessionFactorySimpleQuery() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanup();

		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();

		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P008ProbeEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			assertInstanceOf(
					XuguDialect.class,
					( (SessionFactoryImplementor) sf ).getJdbcServices().getDialect() );

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				P008ProbeEntity e = new P008ProbeEntity();
				e.setId( 1 );
				e.setName( "explicit" );
				session.persist( e );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				String name = session.createQuery(
						"select e.name from P008ProbeEntity e where e.id = 1", String.class )
						.getSingleResult();
				assertEquals( "explicit", name );
			}
		}
		catch ( Exception e ) {
			fail( "explicit dialect IT failed: " + e.getMessage(), e );
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
	void spiAutoResolve_sessionFactoryWithoutExplicitDialect() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanup();

		// No hibernate.dialect — rely on META-INF/services DialectResolver (A-SPI-002)
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();

		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P008ProbeEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			var dialect = ( (SessionFactoryImplementor) sf ).getJdbcServices().getDialect();
			assertInstanceOf( XuguDialect.class, dialect, "SPI should resolve XuguDialect; got " + dialect.getClass() );
			assertEquals( 12, dialect.getVersion().getMajor(), "A-SPI-003 version from live metadata" );

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				P008ProbeEntity e = new P008ProbeEntity();
				e.setId( 2 );
				e.setName( "spi" );
				session.persist( e );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				Long count = session.createQuery(
						"select count(e) from P008ProbeEntity e", Long.class )
						.getSingleResult();
				assertEquals( 1L, count );
			}
		}
		catch ( Exception e ) {
			fail( "SPI auto-resolve IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, a -> {
		} );
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
		}
		catch ( Exception ignored ) {
			// best-effort; gate-on failures surface in tests
		}
	}

	@SuppressWarnings( "unused" )
	private static boolean tableExists(Statement st, String table) throws Exception {
		try ( ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = '" + table + "'" ) ) {
			return rs.next() && rs.getInt( 1 ) > 0;
		}
		catch ( Exception e ) {
			try ( ResultSet rs = st.executeQuery( "SELECT 1 FROM " + table + " WHERE 1=0" ) ) {
				return true;
			}
			catch ( Exception e2 ) {
				return false;
			}
		}
	}
}
