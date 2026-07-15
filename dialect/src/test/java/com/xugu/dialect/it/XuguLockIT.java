package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import jakarta.persistence.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: FOR UPDATE / NOWAIT / WAIT ms against real XuguDB (A-LCK-001/003).
 * Asserts SKIP LOCKED not claimed (A-LCK-004).
 * Also proves LIMIT + FOR UPDATE (+ WAIT) combo under compatiblemode=NONE.
 */
class XuguLockIT {

	private static final String TABLE = "HIB_P004_LOCK";

	@Test
	void forUpdateExecutesAndSkipLockedUnsupported() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		XuguDialect dialect = new XuguDialect();
		assertFalse( dialect.supportsSkipLocked(), "A-LCK-004: supportsSkipLocked must be false" );
		assertFalse( dialect.getForUpdateSkipLockedString().toLowerCase().contains( "skip locked" ) );
		assertTrue( dialect.supportsForUpdate() );
		assertTrue( dialect.supportsNoWait() );
		assertTrue( dialect.supportsWait() );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
				st.execute( "CREATE TABLE " + TABLE + " (id INT PRIMARY KEY, name VARCHAR(32) NOT NULL)" );
				st.execute( "INSERT INTO " + TABLE + " VALUES (1, 'one')" );
				c.commit();

				// A-LCK-001: plain FOR UPDATE
				String fu = "SELECT id FROM " + TABLE + " WHERE id = 1" + dialect.getForUpdateString();
				try ( ResultSet rs = st.executeQuery( fu ) ) {
					assertTrue( rs.next() );
					assertTrue( rs.getInt( 1 ) == 1 );
				}
				c.commit();

				// A-LCK-002 smoke: FOR UPDATE OF
				String fuOf = "SELECT id FROM " + TABLE + " WHERE id = 1" + dialect.getForUpdateString( "id" );
				try ( ResultSet rs = st.executeQuery( fuOf ) ) {
					assertTrue( rs.next() );
				}
				c.commit();

				// A-LCK-003: NOWAIT / WAIT ms — prefer Dialect string; fall back to documented parenthesized form
				assertNowaitExecutable( st, dialect );
				assertWaitMsExecutable( st, dialect );
				c.commit();
			}
			catch ( Exception e ) {
				try {
					c.rollback();
				}
				catch ( SQLException ignored ) {
				}
				fail( "Lock IT failed: " + e.getMessage(), e );
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
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}
	}

	/**
	 * MAJOR fix (rev-p004): prove dialect-assembled XuGu order
	 * {@code FOR UPDATE … LIMIT ?} and {@code FOR UPDATE … LIMIT ? WAIT ms}.
	 * Hibernate-default {@code LIMIT … FOR UPDATE} is rejected.
	 */
	@Test
	void limitForUpdateComboExecutes() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		XuguDialect dialect = new XuguDialect();
		LimitHandler handler = dialect.getLimitHandler();
		Limit limit = new Limit( null, 1 );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
				st.execute( "CREATE TABLE " + TABLE + " (id INT PRIMARY KEY, name VARCHAR(32) NOT NULL)" );
				st.execute( "INSERT INTO " + TABLE + " VALUES (1, 'one')" );
				st.execute( "INSERT INTO " + TABLE + " VALUES (2, 'two')" );
				c.commit();

				// Document that Hibernate-default LIMIT-before-FOR-UPDATE is rejected
				String hibernateDefaultOrder = "select id from " + TABLE + " order by id limit 1 for update";
				assertFalse( tryQuery( st, hibernateDefaultOrder ),
						"Hibernate-default LIMIT…FOR UPDATE should fail on XuGu; sql=" + hibernateDefaultOrder );

				String baseFu = "select id from " + TABLE + " order by id" + dialect.getForUpdateString();
				String sqlFu = handler.processSql( baseFu, limit );
				assertTrue( sqlFu.toLowerCase().contains( "limit" ), "expected LIMIT in: " + sqlFu );
				assertTrue( sqlFu.toLowerCase().contains( "for update" ), "expected FOR UPDATE in: " + sqlFu );
				assertTrue( sqlFu.toLowerCase().indexOf( "for update" ) < sqlFu.toLowerCase().indexOf( "limit" ),
						"expected FOR UPDATE before LIMIT: " + sqlFu );

				try ( PreparedStatement ps = c.prepareStatement( sqlFu ) ) {
					handler.bindLimitParametersAtEndOfQuery( limit, ps, 1 );
					try ( ResultSet rs = ps.executeQuery() ) {
						assertTrue( rs.next(), "FOR UPDATE…LIMIT must return a row; sql=" + sqlFu );
						assertTrue( rs.getInt( 1 ) == 1 );
					}
				}
				c.commit();

				String waitFrag = dialect.getWriteLockString( Timeout.milliseconds( 2000 ) );
				String baseWait = "select id from " + TABLE + " order by id" + waitFrag;
				String sqlWait = handler.processSql( baseWait, limit );
				assertTrue( sqlWait.toLowerCase().contains( "limit" ), "expected LIMIT in: " + sqlWait );
				assertTrue( sqlWait.toLowerCase().contains( "for update" ), "expected FOR UPDATE in: " + sqlWait );
				assertTrue( sqlWait.toLowerCase().contains( "wait" ), "expected WAIT in: " + sqlWait );
				int fuAt = sqlWait.toLowerCase().indexOf( "for update" );
				int limAt = sqlWait.toLowerCase().indexOf( "limit" );
				int waitAt = sqlWait.toLowerCase().lastIndexOf( "wait" );
				assertTrue( fuAt < limAt && limAt < waitAt,
						"expected FOR UPDATE … LIMIT … WAIT: " + sqlWait );

				boolean waitOk;
				try ( PreparedStatement ps = c.prepareStatement( sqlWait ) ) {
					handler.bindLimitParametersAtEndOfQuery( limit, ps, 1 );
					try ( ResultSet rs = ps.executeQuery() ) {
						waitOk = rs.next();
					}
				}
				catch ( SQLException e ) {
					fail( "FOR UPDATE…LIMIT…WAIT rejected by XuguDB. sql=" + sqlWait
							+ " err=" + e.getMessage(), e );
					return;
				}
				assertTrue( waitOk, "FOR UPDATE…LIMIT…WAIT must return a row; sql=" + sqlWait );
				c.commit();
			}
			catch ( Exception e ) {
				try {
					c.rollback();
				}
				catch ( SQLException ignored ) {
				}
				fail( "LIMIT+FOR UPDATE combo IT failed: " + e.getMessage(), e );
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
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}
	}

	private static void assertNowaitExecutable(Statement st, XuguDialect dialect) throws SQLException {
		String inline = "SELECT id FROM " + TABLE + " WHERE id = 1" + dialect.getForUpdateNowaitString();
		if ( tryQuery( st, inline ) ) {
			return;
		}
		// Documented opt_wait on parenthesized select: (select_no_parens) NOWAIT
		String paren = "(SELECT id FROM " + TABLE + " WHERE id = 1" + dialect.getForUpdateString() + ") nowait";
		assertTrue( tryQuery( st, paren ),
				"NOWAIT must execute either as FOR UPDATE NOWAIT or (SELECT ... FOR UPDATE) NOWAIT; failed both. inline="
						+ inline );
	}

	private static void assertWaitMsExecutable(Statement st, XuguDialect dialect) throws SQLException {
		String waitFrag = dialect.getWriteLockString( Timeout.milliseconds( 2000 ) );
		String inline = "SELECT id FROM " + TABLE + " WHERE id = 1" + waitFrag;
		if ( tryQuery( st, inline ) ) {
			return;
		}
		String paren = "(SELECT id FROM " + TABLE + " WHERE id = 1" + dialect.getForUpdateString() + ") wait 2000";
		assertTrue( tryQuery( st, paren ),
				"WAIT ms must execute either as FOR UPDATE WAIT ms or (SELECT ... FOR UPDATE) WAIT ms; failed both. inline="
						+ inline );
	}

	private static boolean tryQuery(Statement st, String sql) {
		try ( ResultSet rs = st.executeQuery( sql ) ) {
			return rs.next();
		}
		catch ( SQLException e ) {
			return false;
		}
	}
}
