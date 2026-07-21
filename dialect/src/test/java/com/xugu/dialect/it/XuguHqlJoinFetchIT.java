package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
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
import com.xugu.dialect.it.entities.I010P015Child;
import com.xugu.dialect.it.entities.I010P015Parent;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT (XP-007 / P-015): HQL {@code join fetch} one-to-many smoke.
 * Tables {@code HIB_I010_P015_*}; gate {@link XuguITGate}.
 */
class XuguHqlJoinFetchIT {

	private static final String PARENT_TABLE = "HIB_I010_P015_PARENT";
	private static final String CHILD_TABLE = "HIB_I010_P015_CHILD";

	@Test
	void hqlJoinFetchInitializesChildrenCollection() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P015Parent.class )
					.addAnnotatedClass( I010P015Child.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				I010P015Parent parent = new I010P015Parent( 1, "p1" );
				parent.addChild( new I010P015Child( 10, "c1" ) );
				parent.addChild( new I010P015Child( 11, "c2" ) );
				parent.addChild( new I010P015Child( 12, "c3" ) );
				session.persist( parent );
				session.getTransaction().commit();
			}

			I010P015Parent loaded;
			try ( Session session = sf.openSession() ) {
				loaded = session.createQuery(
								"select p from Parent p join fetch p.children where p.id = :id",
								I010P015Parent.class )
						.setParameter( "id", 1 )
						.getSingleResult();
				assertNotNull( loaded );
				assertTrue( Hibernate.isInitialized( loaded.getChildren() ),
						"join fetch must initialize children collection" );
				List<I010P015Child> children = loaded.getChildren();
				assertEquals( 3, children.size(), "children size after join fetch" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "HQL join fetch IT failed: " + e.getMessage(), e );
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
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, a -> {
		} );
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			for ( String table : new String[] { CHILD_TABLE, PARENT_TABLE } ) {
				try {
					st.execute( "DROP TABLE IF EXISTS " + table );
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception ignored ) {
		}
	}
}
