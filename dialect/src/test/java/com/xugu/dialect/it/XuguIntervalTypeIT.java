package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguIntervalTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-014 INTERVAL native SQL round-trip.
 *
 * <p>Hibernate 7.4 lacks per-subtype ORM interval JDBC wiring; this IT validates documented
 * XuGu INTERVAL DDL + insert/select on live DB ({@code reference/sql/datatype/datetime.md}).
 * Output format follows server {@code DEF_INTERVAL_STYLE} (default SQL_STANDARD).
 */
class XuguIntervalTypeIT {

	private static final String TABLE = "HIB_I009_P002_INTERVAL";

	@Test
	void intervalNativeRoundTrip_A_TYP_014() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		assertEquals( "interval day to second", XuguIntervalTypeSupport.DURATION_DDL, "A-TYP-014" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
				st.execute( """
						CREATE TABLE %s (
						  id INTEGER PRIMARY KEY,
						  c_ytm INTERVAL YEAR TO MONTH NOT NULL,
						  c_dts INTERVAL DAY TO SECOND NOT NULL,
						  c_sec INTERVAL SECOND NOT NULL
						)
						""".formatted( TABLE ) );

				st.execute( """
						INSERT INTO %s (id, c_ytm, c_dts, c_sec)
						VALUES (1, '1-6', '3 12:48:56', '45.55')
						""".formatted( TABLE ) );

				try ( ResultSet rs = st.executeQuery(
						"SELECT c_ytm, c_dts, c_sec FROM " + TABLE + " WHERE id = 1" ) ) {
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
					st.execute( "DROP TABLE IF EXISTS " + TABLE );
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
}
