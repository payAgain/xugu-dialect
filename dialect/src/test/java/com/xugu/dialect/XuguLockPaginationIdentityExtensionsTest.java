package com.xugu.dialect;

import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.identity.XuguIdentityModeSupport;
import com.xugu.dialect.identity.XuguIdentityModeSupport.IdentityMode;
import com.xugu.dialect.lock.XuguLockTableSupport;
import com.xugu.dialect.lock.XuguLockTableSupport.TableLockMode;
import com.xugu.dialect.lock.XuguLockTableSupport.TableLockWait;
import com.xugu.dialect.pagination.XuguLimitHandler;
import com.xugu.dialect.pagination.XuguPaginationAlternativesSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: A-LCK-006 LOCK TABLE, A-PAG-004 TOP, A-PAG-006 ROWNUM, A-IDN-005 IDENTITY_MODE.
 */
class XuguLockPaginationIdentityExtensionsTest {

	private final XuguDialect dialect = new XuguDialect();
	private final XuguLimitHandler limits = XuguLimitHandler.INSTANCE;

	// -------------------------------------------------------------------------
	// A-LCK-006
	// -------------------------------------------------------------------------

	@Test
	void lockTableSqlMatchesLockDoc_A_LCK_006() {
		assertEquals( 5, XuguLockTableSupport.documentedLockModes().length );

		// lock.md examples
		assertEquals(
				"lock table accounts in exclusive mode",
				XuguLockTableSupport.lockTableSql( "accounts", TableLockMode.EXCLUSIVE, TableLockWait.NONE )
		);
		assertEquals(
				"lock table a in share mode nowait",
				XuguLockTableSupport.lockTableSql( "a", TableLockMode.SHARE, TableLockWait.NOWAIT )
		);
		assertEquals(
				"lock table a in exclusive mode wait 5",
				XuguLockTableSupport.lockTableSql( "a", TableLockMode.EXCLUSIVE, TableLockWait.WAIT_MS, 5 )
		);
		assertEquals(
				"lock table a",
				XuguLockTableSupport.lockTableSql( "a", TableLockMode.DEFAULT, TableLockWait.NONE )
		);
		assertEquals(
				"lock table a wait",
				XuguLockTableSupport.lockTableSql( "a", TableLockMode.DEFAULT, TableLockWait.WAIT )
		);
		assertEquals(
				"lock table a, b in row share mode",
				XuguLockTableSupport.lockTableSql(
						new String[] { "a", "b" }, TableLockMode.ROW_SHARE, TableLockWait.NONE )
		);
		assertEquals(
				"lock table t in row exclusive mode",
				XuguLockTableSupport.lockTableSql( "t", TableLockMode.ROW_EXCLUSIVE, TableLockWait.NONE )
		);
	}

	// -------------------------------------------------------------------------
	// A-PAG-004 / A-PAG-006 — LIMIT remains default
	// -------------------------------------------------------------------------

	@Test
	void limitHandlerRemainsDefault_A_PAG_004_006() {
		assertTrue( dialect.getLimitHandler() instanceof XuguLimitHandler );
		assertFalse( dialect.usesTopPaginationInOrmPath() );
		assertFalse( dialect.usesRownumPaginationInOrmPath() );

		Limit limit = new Limit( null, 10 );
		String sql = limits.processSql( "select id from t order by id", limit );
		assertEquals( "select id from t order by id limit ?", sql );
		assertFalse( sql.toLowerCase().contains( "top " ) );
		assertFalse( sql.toLowerCase().contains( "rownum" ) );
	}

	@Test
	void topSqlMatchesResultsetRestrictedDoc_A_PAG_004() {
		// resultset-restricted.md §TOP — SELECT TOP 2 * FROM tab_top ORDER BY id DESC
		assertEquals(
				"select top 2 * from tab_top order by id desc",
				XuguPaginationAlternativesSupport.selectTopSql( 2, "*", "tab_top", "id desc" )
		);
	}

	@Test
	void rownumSqlMatchesSelectDoc_A_PAG_006() {
		// select.md §8.3 — filter
		assertEquals(
				"select rownum, * from tab_rownum where rownum < 4",
				XuguPaginationAlternativesSupport.rownumFilterSql( "tab_rownum", 4 )
		);
		// select.md §8.3 — ORDER BY in subquery
		assertEquals(
				"select rownum, * from (select * from tab_rownum order by id desc)",
				XuguPaginationAlternativesSupport.rownumOrderedWrapperSql(
						"select * from tab_rownum order by id desc" )
		);
		// offset page wrapper (subquery pattern extension)
		assertEquals(
				"select * from (select rownum rn, t.* from (select * from tab_rownum order by id) t where rownum <= 3) where rn > 1",
				XuguPaginationAlternativesSupport.rownumPageSql(
						"select * from tab_rownum order by id", 3, 1 )
		);
	}

	// -------------------------------------------------------------------------
	// A-IDN-005
	// -------------------------------------------------------------------------

	@Test
	void identityModeSqlMatchesIdentityModeDoc_A_IDN_005() {
		assertEquals( 4, XuguIdentityModeSupport.documentedIdentityModes().length );
		assertTrue( dialect.supportsIdentityModeSessionParameter() );
		assertEquals( "show identity_mode", XuguIdentityModeSupport.SHOW_IDENTITY_MODE_QUERY );

		assertEquals(
				"set identity_mode to NULL_AS_AUTO_INCREMENT",
				XuguIdentityModeSupport.setIdentityModeSql( IdentityMode.NULL_AS_AUTO_INCREMENT )
		);
		assertEquals(
				"set identity_mode to ZERO_AS_AUTO_INCREMENT",
				XuguIdentityModeSupport.setIdentityModeSql( IdentityMode.ZERO_AS_AUTO_INCREMENT )
		);
		assertEquals(
				"alter session set identity_mode = DEFAULT",
				XuguIdentityModeSupport.alterSessionSetIdentityModeSql( IdentityMode.DEFAULT )
		);
	}
}
