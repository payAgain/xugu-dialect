package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;

/**
 * Gated IT: Flyway migrate path on Xugu via Spring Boot (B-FLY-001).
 * Flyway applies {@code db/migration/V1__hib_demo_flyway_marker.sql}; Hibernate keeps
 * {@code ddl-auto=update} for demo entities.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false",
		"spring.flyway.enabled=true",
		"spring.flyway.default-schema=SYSDBA",
		"spring.flyway.create-schemas=false",
		"spring.flyway.validate-on-migrate=false",
		"spring.jpa.hibernate.ddl-auto=update"
})
class DemoFlywayIT {

	private static final String MARKER_TABLE = "HIB_DEMO_FLYWAY_MARKER";
	private static final String HISTORY_TABLE = "flyway_schema_history";

	@Autowired
	private Environment environment;

	@BeforeAll
	static void resetFlywayMarkerState() throws Exception {
		if ( !XuguIntegrationGate.isEnabled() ) {
			return;
		}
		try ( Connection c = DemoXuguJdbc.open(); Statement st = c.createStatement() ) {
			ignoreSql( st, "DROP TABLE IF EXISTS " + MARKER_TABLE );
			ignoreSql( st, "DELETE FROM " + HISTORY_TABLE + " WHERE version = '1'" );
		}
	}

	@AfterEach
	void cleanupMarkerRows() throws Exception {
		try ( Connection c = DemoXuguJdbc.open(); Statement st = c.createStatement() ) {
			st.executeUpdate( "DELETE FROM " + MARKER_TABLE );
		}
	}

	@Test
	void flywayMigratesMarkerTableOnXugu() throws Exception {
		assertTrue( XuguIntegrationGate.isEnabled() );
		assertTrue(
				Boolean.parseBoolean( environment.getProperty( "spring.flyway.enabled", "false" ) ),
				"Flyway must be enabled for this IT" );

		assertTrue( tableExists( MARKER_TABLE ), "Flyway marker table must exist after migrate" );
		assertTrue( flywayHistoryContainsV1(), "flyway_schema_history must record V1" );

		try ( Connection c = DemoXuguJdbc.open(); Statement st = c.createStatement() ) {
			st.executeUpdate(
					"INSERT INTO " + MARKER_TABLE + " (id, marker) VALUES (1, 'p005-flyway-smoke')" );
			try ( ResultSet rs = st.executeQuery(
					"SELECT marker FROM " + MARKER_TABLE + " WHERE marker = 'p005-flyway-smoke'" ) ) {
				assertTrue( rs.next(), "marker row must round-trip" );
				assertEquals( "p005-flyway-smoke", rs.getString( 1 ) );
			}
		}
	}

	private static boolean tableExists(String table) throws Exception {
		try ( Connection c = DemoXuguJdbc.open(); Statement st = c.createStatement() ) {
			st.executeQuery( "SELECT 1 FROM " + table + " WHERE 1=0" );
			return true;
		}
		catch ( Exception e ) {
			return false;
		}
	}

	private static boolean flywayHistoryContainsV1() throws Exception {
		try ( Connection c = DemoXuguJdbc.open(); Statement st = c.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT version FROM " + HISTORY_TABLE + " WHERE version = '1'" ) ) {
			return rs.next();
		}
		catch ( Exception e ) {
			return false;
		}
	}

	private static void ignoreSql(Statement st, String sql) {
		try {
			st.execute( sql );
		}
		catch ( Exception ignored ) {
			// tolerate missing history table on first run
		}
	}
}
