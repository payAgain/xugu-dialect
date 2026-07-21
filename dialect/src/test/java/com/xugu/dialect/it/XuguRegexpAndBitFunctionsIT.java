package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-FUN-019 regexp_* + A-FUN-015 bit_and/bit_or native SQL.
 *
 * <p>SQL shapes from {@code reference/function/string-functions/regexp_*.md} and
 * {@code reference/function/aggregate-functions/bit_*.md}. Bit aggregates use {@code VARBIT}
 * columns per {@code reference/sql/datatype/bit.md} — not verified via ORM entity mapping.
 */
class XuguRegexpAndBitFunctionsIT {

	private static final String BIT_AND_TABLE = "HIB_I009_P006_BIT_AND";
	private static final String BIT_OR_TABLE = "HIB_I009_P006_BIT_OR";

	@Test
	void regexpFunctionsNativeSubset_A_FUN_019() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			try ( Statement st = c.createStatement() ) {

				// regexp_like.md — example 1: 'England or America' ~ 'l.nd' → T
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_LIKE('England or America', 'l.nd') FROM DUAL" ) ) {
					assertTrue( rs.next(), "REGEXP_LIKE row expected" );
					assertTrue( isTruthy( rs, 1 ), "REGEXP_LIKE England: " + rs.getObject( 1 ) );
				}

				// regexp_like.md — example 2: 'MCA' ~ 'BCA' with 'inx' → F
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_LIKE('MCA', 'BCA', 'inx') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertFalse( isTruthy( rs, 1 ), "REGEXP_LIKE MCA inx: " + rs.getObject( 1 ) );
				}

				// regexp_replace.md — example 1: date reorder
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_REPLACE('2023-08-01', '(\\d{4})-(\\d{2})-(\\d{2})', '\\2/\\3/\\1') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "08/01/2023", rs.getString( 1 ).trim() );
				}

				// regexp_replace.md — example 2: mask digits
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_REPLACE('1234567890', '\\d(?=\\d{4})', '*') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "******7890", rs.getString( 1 ).trim() );
				}

				// regexp_substr.md — example 1: first digit run
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_SUBSTR('订单ID: 789, 数量: 456', '[0-9]+') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "789", rs.getString( 1 ).trim() );
				}

				// regexp_substr.md — example 2: start=5, occurrence=2
				try ( ResultSet rs = st.executeQuery(
						"SELECT REGEXP_SUBSTR('a1,b2,c3,d4', '[^,]+', 5, 2) FROM DUAL" ) ) {
					assertTrue( rs.next() );
					assertEquals( "c3", rs.getString( 1 ).trim() );
				}
			}
		}
		catch ( Exception e ) {
			fail( "Regexp functions native IT failed: " + e.getMessage(), e );
		}
	}

	@Test
	void bitAggregatesNativeSubset_A_FUN_015() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + BIT_AND_TABLE );
				st.execute( "CREATE TABLE " + BIT_AND_TABLE + " (c1 VARBIT(7))" );
				st.execute( "INSERT INTO " + BIT_AND_TABLE + " VALUES (b'1010101')(b'1011100')(b'0011100')" );
				try ( ResultSet rs = st.executeQuery( "SELECT BIT_AND(c1) FROM " + BIT_AND_TABLE ) ) {
					assertTrue( rs.next(), "BIT_AND row expected" );
					assertBitLiteralContains( rs.getString( 1 ), "0010100" );
				}

				st.execute( "DROP TABLE IF EXISTS " + BIT_OR_TABLE );
				st.execute( "CREATE TABLE " + BIT_OR_TABLE + " (c1 VARBIT(7))" );
				st.execute( "INSERT INTO " + BIT_OR_TABLE + " VALUES (b'1010101')(b'1011100')(b'0011100')" );
				try ( ResultSet rs = st.executeQuery( "SELECT BIT_OR(c1) FROM " + BIT_OR_TABLE ) ) {
					assertTrue( rs.next(), "BIT_OR row expected" );
					assertBitLiteralContains( rs.getString( 1 ), "1011101" );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "DROP TABLE IF EXISTS " + BIT_AND_TABLE );
					st.execute( "DROP TABLE IF EXISTS " + BIT_OR_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "Bit aggregate native IT failed: " + e.getMessage(), e );
		}
	}

	private static boolean isTruthy(ResultSet rs, int column) throws Exception {
		Object value = rs.getObject( column );
		if ( value instanceof Boolean b ) {
			return b;
		}
		if ( value instanceof Number n ) {
			return n.intValue() != 0;
		}
		String s = String.valueOf( value ).trim();
		return "T".equalsIgnoreCase( s ) || "TRUE".equalsIgnoreCase( s ) || "1".equals( s );
	}

	private static void assertBitLiteralContains(String actual, String expectedBits) {
		String normalized = actual == null ? "" : actual.replaceAll( "[^01]", "" );
		assertEquals( expectedBits, normalized, "bit literal: " + actual );
	}
}
