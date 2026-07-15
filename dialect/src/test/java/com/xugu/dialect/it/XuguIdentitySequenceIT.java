package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.P005IdentityEntity;
import com.xugu.dialect.it.entities.P005SequenceEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: IDENTITY persist + id backfill; SEQUENCE generator; NEXTVAL/CURRVAL/DUAL (P-005).
 */
class XuguIdentitySequenceIT {

	private static final String IDN_TABLE = "HIB_P005_IDN";
	private static final String SEQ_TABLE = "HIB_P005_SEQ_ENT";
	private static final String SEQ_NAME = "HIB_P005_SEQ_GEN";

	@Test
	void identityPersistBackfillsId_A_IDN_003_004() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P005IdentityEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );

			sf = metadata.buildSessionFactory();
			Long id;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				P005IdentityEntity e = new P005IdentityEntity();
				e.setName( "idn-1" );
				session.persist( e );
				session.flush();
				id = e.getId();
				assertNotNull( id, "IDENTITY id must be backfilled after persist/flush" );
				assertTrue( id > 0, "expected positive identity id, got " + id );
				session.getTransaction().commit();
			}

			try ( Connection c = XuguTestConnection.open();
					PreparedStatement ps = c.prepareStatement(
							"SELECT name FROM " + IDN_TABLE + " WHERE id = ?" ) ) {
				ps.setLong( 1, id );
				try ( ResultSet rs = ps.executeQuery() ) {
					assertTrue( rs.next() );
					assertEquals( "idn-1", rs.getString( 1 ) );
				}
			}

			// Documented select fallback still works after JDBC insert path
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.executeUpdate( "INSERT INTO " + IDN_TABLE + " (name) VALUES ('idn-jdbc')" );
				try ( ResultSet rs = st.executeQuery( "SELECT LAST_INSERT_ID() FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertNotNull( rs.getObject( 1 ) );
				}
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			fail( "Identity IT failed: " + e.getMessage(), e );
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
	void sequenceGeneratorPersist_A_SEQ_003_004_008() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		// Prove locked NEXTVAL / CURRVAL / DUAL forms before Hibernate path
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "CREATE SEQUENCE " + SEQ_NAME + " START WITH 10 INCREMENT BY 1" );
			try ( ResultSet rs = st.executeQuery( "SELECT " + SEQ_NAME + ".NEXTVAL FROM DUAL" ) ) {
				assertTrue( rs.next() );
				assertEquals( 10L, rs.getLong( 1 ) );
			}
			try ( ResultSet rs = st.executeQuery( "SELECT CURRVAL('" + SEQ_NAME + "') FROM DUAL" ) ) {
				assertTrue( rs.next() );
				assertEquals( 10L, rs.getLong( 1 ) );
			}
			st.execute( "DROP SEQUENCE " + SEQ_NAME );
		}
		catch ( Exception e ) {
			fail( "Sequence SQL probe failed: " + e.getMessage(), e );
		}

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P005SequenceEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );

			sf = metadata.buildSessionFactory();
			Long id1;
			Long id2;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				P005SequenceEntity a = new P005SequenceEntity();
				a.setName( "seq-a" );
				session.persist( a );
				P005SequenceEntity b = new P005SequenceEntity();
				b.setName( "seq-b" );
				session.persist( b );
				session.flush();
				id1 = a.getId();
				id2 = b.getId();
				assertNotNull( id1 );
				assertNotNull( id2 );
				assertTrue( id2 > id1, "sequence ids should increase: " + id1 + " then " + id2 );
				session.getTransaction().commit();
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			fail( "Sequence generator IT failed: " + e.getMessage(), e );
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
				.applySetting( JdbcSettings.USE_GET_GENERATED_KEYS, "true" )
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
			ignore( st, "DROP TABLE IF EXISTS " + IDN_TABLE );
			ignore( st, "DROP TABLE IF EXISTS " + SEQ_TABLE );
			ignore( st, "DROP SEQUENCE IF EXISTS " + SEQ_NAME );
		}
		catch ( Exception ignored ) {
		}
	}

	private static void ignore(Statement st, String sql) {
		try {
			st.execute( sql );
		}
		catch ( Exception ignored ) {
		}
	}
}
