package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated live assert for A-XCUT-001: unquoted identifiers fold to UPPER on XuguDB.
 */
class XuguIdentifierFoldingIT {

	private static final String TABLE_FOLDED = "HIB_P003_FOLD";

	@Test
	void unquotedIdentifiersFoldToUppercase_A_XCUT_001() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE_FOLDED );
			st.execute( "CREATE TABLE hib_p003_fold (id INTEGER PRIMARY KEY, v INTEGER NOT NULL)" );
			st.execute( "INSERT INTO hib_p003_fold (id, v) VALUES (1, 7)" );

			try ( ResultSet rs = st.executeQuery(
					"SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = '" + TABLE_FOLDED + "'" ) ) {
				assertTrue( rs.next() );
				assertEquals( 1, rs.getInt( 1 ), "unquoted table name should fold to uppercase metadata name" );
			}

			try ( ResultSet rs = st.executeQuery( "SELECT v FROM " + TABLE_FOLDED + " WHERE id = 1" ) ) {
				assertTrue( rs.next() );
				assertEquals( 7, rs.getInt( 1 ) );
			}

			st.execute( "DROP TABLE IF EXISTS " + TABLE_FOLDED );
		}
		catch ( Exception e ) {
			fail( "Identifier folding live IT failed: " + e.getMessage(), e );
		}
	}
}
