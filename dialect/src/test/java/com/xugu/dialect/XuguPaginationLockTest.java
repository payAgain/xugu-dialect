package com.xugu.dialect;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Timeouts;
import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.pagination.XuguLimitHandler;

import jakarta.persistence.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for P-004 pagination + lock SQL fragments.
 */
class XuguPaginationLockTest {

	private final XuguDialect dialect = new XuguDialect();
	private final XuguLimitHandler limits = XuguLimitHandler.INSTANCE;

	@Test
	void limitHandlerIsXuguAndSupportsLimitOffset() {
		assertTrue( dialect.getLimitHandler() instanceof XuguLimitHandler );
		assertTrue( limits.supportsLimit() );
		assertTrue( limits.supportsOffset() );
		assertTrue( limits.supportsLimitOffset() );
		assertTrue( limits.supportsVariableLimit() );
		assertTrue( limits.bindLimitParametersInReverseOrder() );
	}

	@Test
	void limitOnlyUsesBindMarker_A_PAG_001_003() {
		Limit limit = new Limit( null, 10 );
		String sql = limits.processSql( "select id from t order by id", limit );
		assertEquals( "select id from t order by id limit ?", sql );
		assertFalse( sql.toLowerCase().contains( "fetch first" ), "must not emit FETCH FIRST (A-PAG-005)" );
	}

	@Test
	void limitOffsetStableForm_A_PAG_002() {
		Limit limit = new Limit( 5, 10 );
		String sql = limits.processSql( "select id from t order by id", limit );
		assertEquals( "select id from t order by id limit ? offset ?", sql );
		assertFalse( sql.contains( "limit ?,?" ) || sql.matches( "(?i).*limit \\?,\\?.*" ) );
		assertFalse( sql.toLowerCase().contains( "fetch first" ) );
	}

	@Test
	void limitAppendsAfterForUpdate_XuGuOrder() {
		Limit limit = new Limit( null, 3 );
		String sql = limits.processSql( "select id from t for update", limit );
		assertEquals( "select id from t for update limit ?", sql );
	}

	@Test
	void limitBeforeTrailingWait_XuGuOrder() {
		Limit limit = new Limit( null, 2 );
		String sql = limits.processSql( "select id from t for update wait 2000", limit );
		assertEquals( "select id from t for update limit ? wait 2000", sql );
		String nowait = limits.processSql( "select id from t for update nowait", limit );
		assertEquals( "select id from t for update limit ? nowait", nowait );
	}

	@Test
	void forUpdateBasic_A_LCK_001() {
		assertEquals( " for update", dialect.getForUpdateString() );
		assertEquals( " for update", dialect.getWriteLockString( Timeouts.WAIT_FOREVER ) );
	}

	@Test
	void forUpdateOf_A_LCK_002() {
		assertEquals( " for update of t_.id", dialect.getForUpdateString( "t_.id" ) );
		LockOptions opts = new LockOptions( LockMode.PESSIMISTIC_WRITE );
		assertEquals( " for update of t_.id", dialect.getForUpdateString( "t_.id", opts ) );
	}

	@Test
	void nowaitAndWaitMilliseconds_A_LCK_003() {
		assertEquals( " for update nowait", dialect.getForUpdateNowaitString() );
		assertEquals( " for update of t_.id nowait", dialect.getForUpdateNowaitString( "t_.id" ) );
		assertEquals( " for update nowait", dialect.getWriteLockString( Timeouts.NO_WAIT ) );
		assertEquals( " for update nowait", dialect.getWriteLockString( 0 ) );

		// XuGu WAIT is milliseconds — Hibernate Timeout.ms pass through (no seconds conversion)
		assertEquals( " for update wait 2000", dialect.getWriteLockString( Timeout.milliseconds( 2000 ) ) );
		assertEquals( " for update wait 2000", dialect.getWriteLockString( 2000 ) );
		assertEquals( " for update of t_.id wait 500",
				dialect.getWriteLockString( "t_.id", Timeout.milliseconds( 500 ) ) );
		assertEquals( " for update wait 2000", dialect.getForUpdateString( Timeout.milliseconds( 2000 ) ) );
	}

	@Test
	void skipLockedNotSupported_A_LCK_004() {
		assertFalse( dialect.supportsSkipLocked() );
		assertFalse( dialect.getForUpdateSkipLockedString().toLowerCase().contains( "skip locked" ) );
		assertFalse( dialect.getForUpdateSkipLockedString( "t_.id" ).toLowerCase().contains( "skip locked" ) );
		// even if write path were forced with SKIP_LOCKED magic, do not invent keyword
		assertEquals( " for update", dialect.getWriteLockString( Timeouts.SKIP_LOCKED ) );
	}

	@Test
	void noForShare_A_LCK_005() {
		String read = dialect.getReadLockString( Timeouts.WAIT_FOREVER );
		assertFalse( read.toLowerCase().contains( "share" ) );
		assertTrue( read.toLowerCase().contains( "for update" ) );
		assertTrue( dialect.supportsNoWait() );
		assertTrue( dialect.supportsWait() );
		assertTrue( dialect.supportsForUpdate() );
	}
}
