package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: LIMIT / LIMIT+OFFSET against real XuguDB (A-PAG-001/002/003).
 */
class XuguPaginationIT {

	private static final String TABLE = "HIB_P004_PAGE";

	@Test
	void limitAndOffsetReturnExpectedRows() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		XuguDialect dialect = new XuguDialect();
		LimitHandler handler = dialect.getLimitHandler();

		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
			st.execute( "CREATE TABLE " + TABLE + " (id INT PRIMARY KEY, name VARCHAR(32) NOT NULL)" );
			for ( int i = 1; i <= 10; i++ ) {
				st.execute( "INSERT INTO " + TABLE + " VALUES (" + i + ", 'r" + i + "')" );
			}

			// A-PAG-001: LIMIT only
			List<Integer> firstThree = queryIds( c, handler, "select id from " + TABLE + " order by id", null, 3 );
			assertEquals( List.of( 1, 2, 3 ), firstThree );

			// A-PAG-002: LIMIT count OFFSET offset (stable form)
			List<Integer> page = queryIds( c, handler, "select id from " + TABLE + " order by id", 4, 3 );
			assertEquals( List.of( 5, 6, 7 ), page );

			assertFalse( dialect.supportsSkipLocked(), "A-LCK-004 must remain unsupported" );
		}
		catch ( Exception e ) {
			fail( "Pagination IT failed: " + e.getMessage(), e );
		}
		finally {
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
			}
			catch ( Exception ignored ) {
			}
		}
	}

	private static List<Integer> queryIds(Connection c, LimitHandler handler, String baseSql, Integer firstRow, Integer maxRows)
			throws Exception {
		Limit limit = new Limit( firstRow, maxRows );
		String sql = handler.processSql( baseSql, limit );
		assertTrue( sql.toLowerCase().contains( "limit" ), "expected LIMIT clause: " + sql );
		assertFalse( sql.toLowerCase().contains( "fetch first" ), "must not emit FETCH FIRST: " + sql );

		try ( PreparedStatement ps = c.prepareStatement( sql ) ) {
			handler.bindLimitParametersAtEndOfQuery( limit, ps, 1 );
			List<Integer> ids = new ArrayList<>();
			try ( ResultSet rs = ps.executeQuery() ) {
				while ( rs.next() ) {
					ids.add( rs.getInt( 1 ) );
				}
			}
			return ids;
		}
	}
}
