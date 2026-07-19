package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.sequence.XuguSequenceSupport;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-SEQ-006 ALTER SEQUENCE (START WITH / INCREMENT BY — not RESTART WITH).
 */
class XuguAlterSequenceIT {

	private static final String SEQ = "HIB_I007_P004_ALT_SEQ";
	private static final XuguSequenceSupport SUPPORT = XuguSequenceSupport.INSTANCE;

	@Test
	void alterSequenceStartWithAndIncrement_A_SEQ_006() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		String restartSql = SUPPORT.getRestartSequenceString( SEQ, 200L );
		assertEquals( "alter sequence " + SEQ.toLowerCase() + " start with 200", restartSql.toLowerCase() );
		assertFalse( restartSql.toLowerCase().contains( "restart with" ),
				"XuGu rejects RESTART WITH (E19132); use START WITH" );

		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP SEQUENCE IF EXISTS " + SEQ );
			st.execute( "CREATE SEQUENCE " + SEQ + " START WITH 1 INCREMENT BY 1" );

			assertNextVal( st, 1L );
			assertNextVal( st, 2L );

			st.execute( restartSql );
			st.execute( XuguSequenceSupport.alterSequenceIncrementBy( SEQ, 10 ) );

			long afterStart = nextVal( st );
			assertTrue( afterStart >= 200L, "after START WITH 200, nextval should be >= 200, got " + afterStart );
			long stepped = nextVal( st );
			assertEquals( afterStart + 10L, stepped, "INCREMENT BY 10 should apply after alter" );
		}
		catch ( Exception e ) {
			fail( "A-SEQ-006 ALTER SEQUENCE IT failed: " + e.getMessage(), e );
		}
		finally {
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "DROP SEQUENCE IF EXISTS " + SEQ );
			}
			catch ( Exception ignored ) {
			}
		}
	}

	private static void assertNextVal(Statement st, long expected) throws Exception {
		assertEquals( expected, nextVal( st ) );
	}

	private static long nextVal(Statement st) throws Exception {
		try ( ResultSet rs = st.executeQuery( "SELECT " + SEQ + ".NEXTVAL FROM DUAL" ) ) {
			assertTrue( rs.next() );
			return rs.getLong( 1 );
		}
	}
}
