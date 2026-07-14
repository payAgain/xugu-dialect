package com.xugu.dialect.it;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Real-DB type round-trip + JDBC transaction smoke (gated).
 */
@TestMethodOrder( MethodOrderer.OrderAnnotation.class )
class XuguTypeRoundTripIT {

	private static final String TABLE = "HIB_P003_TYPE_RT";

	@Test
	@Order( 1 )
	void dbReachableWhenGateEnabled() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		try ( Connection c = XuguTestConnection.open();
				Statement s = c.createStatement();
				ResultSet rs = s.executeQuery( "SELECT 1 FROM DUAL" ) ) {
			assertTrue( rs.next() );
			assertEquals( 1, rs.getInt( 1 ) );
		}
		catch ( Exception e ) {
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}
	}

	@Test
	@Order( 2 )
	void typeRoundTripKeyTypes() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
				st.execute( """
						CREATE TABLE %s (
						  id INTEGER PRIMARY KEY,
						  c_int INTEGER NOT NULL,
						  c_dec NUMERIC(12,2) NOT NULL,
						  c_vc VARCHAR(64) NOT NULL,
						  c_bool BOOLEAN NOT NULL,
						  c_date DATE NOT NULL,
						  c_ts TIMESTAMP(3) NOT NULL,
						  c_bin BINARY,
						  c_blob BLOB,
						  c_clob CLOB,
						  c_guid GUID,
						  c_json JSON
						)
						""".formatted( TABLE ) );

				UUID guid = UUID.fromString( "550e8400-e29b-41d4-a716-446655440000" );
				String json = "{\"k\":1,\"s\":\"xugu\"}";
				byte[] bin = new byte[] { 0x01, 0x02, 0x03, 0x04 };
				LocalDate day = LocalDate.of( 2026, 7, 14 );
				LocalDateTime ts = LocalDateTime.of( 2026, 7, 14, 17, 30, 15 );

				try ( PreparedStatement ps = c.prepareStatement( """
						INSERT INTO %s (id,c_int,c_dec,c_vc,c_bool,c_date,c_ts,c_bin,c_blob,c_clob,c_guid,c_json)
						VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
						""".formatted( TABLE ) ) ) {
					ps.setInt( 1, 1 );
					ps.setInt( 2, 42 );
					ps.setBigDecimal( 3, new BigDecimal( "1234.56" ) );
					ps.setString( 4, "hello-xugu" );
					ps.setBoolean( 5, true );
					ps.setDate( 6, Date.valueOf( day ) );
					ps.setTimestamp( 7, Timestamp.valueOf( ts ) );
					ps.setBytes( 8, bin );
					ps.setBytes( 9, bin );
					ps.setString( 10, "clob-text" );
					ps.setObject( 11, guid.toString().replace( "-", "" ) );
					ps.setString( 12, json );
					assertEquals( 1, ps.executeUpdate() );
				}

				try ( PreparedStatement ps = c.prepareStatement(
						"SELECT c_int,c_dec,c_vc,c_bool,c_date,c_ts,c_bin,c_blob,c_clob,c_guid,c_json FROM "
								+ TABLE + " WHERE id=1" );
						ResultSet rs = ps.executeQuery() ) {
					assertTrue( rs.next() );
					assertEquals( 42, rs.getInt( 1 ) );
					assertEquals( 0, new BigDecimal( "1234.56" ).compareTo( rs.getBigDecimal( 2 ) ) );
					assertEquals( "hello-xugu", rs.getString( 3 ) );
					assertTrue( rs.getBoolean( 4 ) );
					assertEquals( day, rs.getDate( 5 ).toLocalDate() );
					assertEquals( ts, rs.getTimestamp( 6 ).toLocalDateTime() );
					assertArrayEquals( bin, rs.getBytes( 7 ) );
					assertArrayEquals( bin, rs.getBytes( 8 ) );
					assertEquals( "clob-text", rs.getString( 9 ) );
					assertNotNull( rs.getObject( 10 ) );
					String gotJson = rs.getString( 11 );
					assertNotNull( gotJson );
					assertTrue( gotJson.contains( "\"k\"" ) || gotJson.contains( "k" ) );
					assertFalse( rs.next() );
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
				catch ( Exception cleanup ) {
					// best-effort cleanup
				}
			}
		}
	}

	@Test
	@Order( 3 )
	void jdbcTransactionCommitRollbackSmoke() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		String table = "HIB_P003_TX";
		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + table );
				st.execute( "CREATE TABLE " + table + " (id INTEGER PRIMARY KEY, v INTEGER NOT NULL)" );
				st.execute( "INSERT INTO " + table + " VALUES (1, 10)" );
				c.commit();

				st.execute( "UPDATE " + table + " SET v = 99 WHERE id = 1" );
				c.rollback();

				try ( ResultSet rs = st.executeQuery( "SELECT v FROM " + table + " WHERE id = 1" ) ) {
					assertTrue( rs.next() );
					assertEquals( 10, rs.getInt( 1 ) );
				}

				st.execute( "UPDATE " + table + " SET v = 20 WHERE id = 1" );
				c.commit();
				try ( ResultSet rs = st.executeQuery( "SELECT v FROM " + table + " WHERE id = 1" ) ) {
					assertTrue( rs.next() );
					assertEquals( 20, rs.getInt( 1 ) );
				}
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "DROP TABLE IF EXISTS " + table );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}

	@Test
	@Order( 4 )
	void illegalTypeFailsDiagnosablyAndCleansUp() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		String table = "HIB_P003_BAD";
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + table );
			boolean failed = false;
			try {
				st.execute( "CREATE TABLE " + table + " (id INTEGER PRIMARY KEY, bad NOT_A_REAL_TYPE)" );
			}
			catch ( Exception e ) {
				failed = true;
				assertNotNull( e.getMessage() );
				assertFalse( e.getMessage().isBlank() );
			}
			assertTrue( failed, "expected DDL failure for illegal type" );
			// ensure no leftover table
			st.execute( "DROP TABLE IF EXISTS " + table );
		}
	}
}
