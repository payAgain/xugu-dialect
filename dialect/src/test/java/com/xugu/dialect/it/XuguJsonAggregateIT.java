package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.QuerySettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.aggregate.XuguAggregateSupport;
import com.xugu.dialect.it.entities.I003P003JsonEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ORM entrypoint IT for C-JSON-001…004: JSON column round-trip + HQL json_arrayagg /
 * json_objectagg on live XuguDB.
 */
class XuguJsonAggregateIT {

	private static final String TABLE = "HIB_I003_P003_JSON";

	@Test
	void jsonColumnRoundTripAndHqlAggregates() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		assertSame( XuguAggregateSupport.INSTANCE, new XuguDialect().getAggregateSupport() );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I003P003JsonEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I003P003JsonEntity( 1, "g1", "a", "{\"n\":1}" ) );
				session.persist( new I003P003JsonEntity( 2, "g1", "b", "{\"n\":2}" ) );
				session.persist( new I003P003JsonEntity( 3, "g2", "c", "{\"n\":3}" ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I003P003JsonEntity loaded = session.find( I003P003JsonEntity.class, 1 );
				assertNotNull( loaded );
				assertNotNull( loaded.getPayload() );
				assertTrue( loaded.getPayload().toLowerCase( Locale.ROOT ).contains( "1" )
						|| loaded.getPayload().contains( "n" ) );

				List<String> arrays = session.createQuery(
						"select cast(json_arrayagg(e.label) as string) from I003P003JsonEntity e "
								+ "where e.grpKey = 'g1' group by e.grpKey",
						String.class )
						.getResultList();
				assertEquals( 1, arrays.size() );
				String arr = arrays.get( 0 );
				assertNotNull( arr );
				assertTrue( arr.contains( "a" ) || arr.contains( "A" ), "arrayagg should include label a: " + arr );
				assertTrue( arr.contains( "b" ) || arr.contains( "B" ), "arrayagg should include label b: " + arr );

				List<String> objects = session.createQuery(
						"select cast(json_objectagg(e.label, e.grpKey) as string) from I003P003JsonEntity e "
								+ "where e.id = 3 group by e.grpKey",
						String.class )
						.getResultList();
				assertEquals( 1, objects.size() );
				assertNotNull( objects.get( 0 ) );
				assertTrue( objects.get( 0 ).contains( "c" ) || objects.get( 0 ).contains( "C" )
						|| objects.get( 0 ).contains( "g2" ), "objectagg result: " + objects.get( 0 ) );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "JSON/Aggregate IT failed: " + e.getMessage(), e );
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
				.applySetting( QuerySettings.JSON_FUNCTIONS_ENABLED, "true" )
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
