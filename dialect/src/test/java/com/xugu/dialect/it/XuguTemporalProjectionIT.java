package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
import com.xugu.dialect.it.entities.I010P016TemporalEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: temporal projection materialization (XP-009).
 *
 * <p>Only matrix-allowed functions: {@code year}/{@code month}/{@code day}/{@code extract}/
 * {@code current_date}/{@code current_timestamp}. Seed datetime is fixed.
 *
 * <p>I-010/P-016 — do not invent undocumented date/time functions.
 */
class XuguTemporalProjectionIT {

	private static final String TABLE = "HIB_I010_P016_TEMP";

	/** Fixed seed: 2024-03-15 12:30:00 */
	private static final LocalDateTime SEED = LocalDateTime.of( 2024, 3, 15, 12, 30, 0 );

	@Test
	void yearMonthDayExtractAndCurrentTemporalProject() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanup();

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P016TemporalEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P016TemporalEntity( 1, "seed", SEED ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				Number year = session.createQuery(
						"select year(e.createdAt) from I010P016TemporalEntity e where e.id = 1",
						Number.class )
						.getSingleResult();
				assertNotNull( year );
				assertEquals( 2024, year.intValue() );

				Number month = session.createQuery(
						"select month(e.createdAt) from I010P016TemporalEntity e where e.id = 1",
						Number.class )
						.getSingleResult();
				assertNotNull( month );
				assertEquals( 3, month.intValue() );

				Number day = session.createQuery(
						"select day(e.createdAt) from I010P016TemporalEntity e where e.id = 1",
						Number.class )
						.getSingleResult();
				assertNotNull( day );
				assertEquals( 15, day.intValue() );

				Number extractYear = session.createQuery(
						"select extract(year from e.createdAt) from I010P016TemporalEntity e where e.id = 1",
						Number.class )
						.getSingleResult();
				assertNotNull( extractYear );
				assertEquals( 2024, extractYear.intValue() );

				Object currentDate = session.createQuery(
						"select current_date from I010P016TemporalEntity e where e.id = 1",
						Object.class )
						.getSingleResult();
				assertNotNull( currentDate );
				assertReasonableCurrentDate( currentDate );

				Object currentTs = session.createQuery(
						"select current_timestamp from I010P016TemporalEntity e where e.id = 1",
						Object.class )
						.getSingleResult();
				assertNotNull( currentTs );
				assertReasonableCurrentTimestamp( currentTs );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Temporal projection IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	private static void assertReasonableCurrentDate(Object value) {
		if ( value instanceof LocalDate localDate ) {
			assertTrue( localDate.getYear() >= 2020, "current_date year=" + localDate.getYear() );
			return;
		}
		if ( value instanceof Date date ) {
			assertTrue( date.getTime() > 0L, "current_date epoch=" + date.getTime() );
			return;
		}
		if ( value instanceof java.sql.Date sqlDate ) {
			assertTrue( sqlDate.toLocalDate().getYear() >= 2020, "sql current_date=" + sqlDate );
			return;
		}
		// JDBC may surface as string-like; accept non-blank
		String text = String.valueOf( value ).trim();
		assertTrue( !text.isEmpty() && !text.equalsIgnoreCase( "null" ),
				"unexpected current_date type/value: " + value.getClass() + "=" + value );
	}

	private static void assertReasonableCurrentTimestamp(Object value) {
		if ( value instanceof LocalDateTime ldt ) {
			assertTrue( ldt.getYear() >= 2020, "current_timestamp year=" + ldt.getYear() );
			return;
		}
		if ( value instanceof Date date ) {
			assertTrue( date.getTime() > 0L, "current_timestamp epoch=" + date.getTime() );
			return;
		}
		String text = String.valueOf( value ).trim();
		assertTrue( !text.isEmpty() && !text.equalsIgnoreCase( "null" ),
				"unexpected current_timestamp type/value: " + value.getClass() + "=" + value );
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
