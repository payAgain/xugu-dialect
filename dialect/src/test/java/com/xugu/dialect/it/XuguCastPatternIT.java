package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.hibernate.query.sqm.CastType;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated live assert for A-TYP-019: {@code CAST(... AS ...)} on XuguDB matches dialect {@code castPattern}.
 */
class XuguCastPatternIT {

	@Test
	void castExpressionOnLiveDb_A_TYP_019() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		XuguDialect dialect = new XuguDialect();
		String pattern = dialect.castPattern( CastType.STRING, CastType.INTEGER );
		assertNotNull( pattern );
		String lower = pattern.toLowerCase();
		assertTrue( lower.contains( "cast" ), "castPattern should use CAST syntax: " + pattern );

		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			try ( ResultSet rs = st.executeQuery( "SELECT CAST('42' AS INTEGER) FROM DUAL" ) ) {
				assertTrue( rs.next() );
				assertEquals( 42, rs.getInt( 1 ) );
			}
		}
		catch ( Exception e ) {
			fail( "CAST live IT failed: " + e.getMessage(), e );
		}
	}
}
