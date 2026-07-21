package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
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
import com.xugu.dialect.it.entities.I010P002IntervalEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguIntervalJdbcType;
import com.xugu.dialect.type.XuguIntervalTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-014 INTERVAL — native SQL + entity ORM Duration round-trip.
 *
 * <p>Native path validates documented XuGu INTERVAL DDL + insert/select.
 * Entity path uses {@link XuguIntervalJdbcType} for {@code SqlTypes.DURATION} /
 * {@code INTERVAL_SECOND} persist/load (I-010/P-002). Output format follows server
 * {@code DEF_INTERVAL_STYLE} (default SQL_STANDARD).
 */
class XuguIntervalTypeIT {

	private static final String NATIVE_TABLE = "HIB_I009_P002_INTERVAL";
	private static final String ENTITY_TABLE = "HIB_I010_P002_INTERVAL";

	@Test
	void intervalNativeRoundTrip_A_TYP_014() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		assertEquals( "interval day to second", XuguIntervalTypeSupport.DURATION_DDL, "A-TYP-014" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + NATIVE_TABLE );
				st.execute( """
						CREATE TABLE %s (
						  id INTEGER PRIMARY KEY,
						  c_ytm INTERVAL YEAR TO MONTH NOT NULL,
						  c_dts INTERVAL DAY TO SECOND NOT NULL,
						  c_sec INTERVAL SECOND NOT NULL
						)
						""".formatted( NATIVE_TABLE ) );

				st.execute( """
						INSERT INTO %s (id, c_ytm, c_dts, c_sec)
						VALUES (1, '1-6', '3 12:48:56', '45.55')
						""".formatted( NATIVE_TABLE ) );

				try ( ResultSet rs = st.executeQuery(
						"SELECT c_ytm, c_dts, c_sec FROM " + NATIVE_TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next(), "expected interval row" );
					String ytm = rs.getString( 1 );
					String dts = rs.getString( 2 );
					String sec = rs.getString( 3 );
					assertNotNull( ytm );
					assertNotNull( dts );
					assertNotNull( sec );
					assertTrue( ytm.contains( "1" ) && ytm.contains( "6" ),
							"year-to-month round-trip: " + ytm );
					assertTrue( dts.contains( "3" ) && dts.contains( "12" ),
							"day-to-second round-trip: " + dts );
					assertTrue( sec.contains( "45" ) || sec.contains( "0:00:45" ) || sec.contains( ":45" ),
							"second round-trip: " + sec );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "DROP TABLE IF EXISTS " + NATIVE_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "INTERVAL native IT failed: " + e.getMessage(), e );
		}
	}

	@Test
	void intervalEntityOrmRoundTrip_A_TYP_014() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanupEntityTable();

		final Duration dayToSecond = Duration.ofDays( 3 )
				.plusHours( 12 )
				.plusMinutes( 48 )
				.plusSeconds( 56 );
		final Duration secondsOnly = Duration.ofSeconds( 45, 550_000_000L );

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P002IntervalEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P002IntervalEntity( 1, dayToSecond, secondsOnly ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I010P002IntervalEntity loaded = session.find( I010P002IntervalEntity.class, 1 );
				assertNotNull( loaded, "entity must load" );
				assertNotNull( loaded.getDuration(), "DURATION column" );
				assertNotNull( loaded.getIntervalSecond(), "INTERVAL_SECOND column" );
				assertTrue(
						XuguIntervalJdbcType.approximatelyEqual( dayToSecond, loaded.getDuration() ),
						"DURATION round-trip expected=" + dayToSecond + " actual=" + loaded.getDuration() );
				assertTrue(
						XuguIntervalJdbcType.approximatelyEqual( secondsOnly, loaded.getIntervalSecond() ),
						"INTERVAL_SECOND round-trip expected=" + secondsOnly
								+ " actual=" + loaded.getIntervalSecond() );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "INTERVAL entity ORM IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanupEntityTable();
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

	private static void cleanupEntityTable() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + ENTITY_TABLE );
		}
		catch ( Exception ignored ) {
		}
	}
}
