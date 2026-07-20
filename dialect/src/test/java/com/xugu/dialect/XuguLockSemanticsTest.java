package com.xugu.dialect;

import java.util.Locale;
import java.util.stream.Stream;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Timeouts;
import org.hibernate.dialect.lock.spi.LockingSupport;
import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.xugu.dialect.internal.XuguLockingSupport;
import com.xugu.dialect.pagination.XuguLimitHandler;

import jakarta.persistence.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * I-008 / P-005 — lock semantics behavioral + negative evidence (offline).
 *
 * <p>Proves XuGu dialect does <b>not</b> emit {@code SKIP LOCKED} or {@code FOR SHARE}, and that
 * {@code PESSIMISTIC_READ} maps to exclusive {@code FOR UPDATE} SQL shape per P-002 docs.
 *
 * @see docs/user-guide/07-lock-integration.md
 */
class XuguLockSemanticsTest {

	private static final String USER_GUIDE_LOCK = "docs/user-guide/07-lock-integration.md";

	private final XuguDialect dialect = new XuguDialect();
	private final XuguLimitHandler limits = XuguLimitHandler.INSTANCE;

	// -------------------------------------------------------------------------
	// Negative — forbidden keywords never appear in lock SQL fragments
	// -------------------------------------------------------------------------

	@ParameterizedTest(name = "forbidden absent: {0}")
	@MethodSource("allLockSqlFragments")
	void generatedLockSql_neverContainsSkipLockedOrForShare(String label, String sqlFragment) {
		assertNoForbiddenLockKeywords( label, sqlFragment );
	}

	@Test
	void lockingSupport_declaresSkipLockedFalse_A_LCK_004() {
		LockingSupport support = dialect.getLockingSupport();
		assertEquals( XuguLockingSupport.INSTANCE, support );
		assertFalse( dialect.supportsSkipLocked(), "A-LCK-004 / C-SKIP-001 — " + USER_GUIDE_LOCK );
	}

	@Test
	void skipLockedLockMode_stillEmitsPlainForUpdate_A_LCK_004() {
		LockOptions skip = new LockOptions( LockMode.UPGRADE_SKIPLOCKED );
		String withAlias = dialect.getForUpdateString( "t_.id", skip );
		assertNoForbiddenLockKeywords( "UPGRADE_SKIPLOCKED+alias", withAlias );
		assertEquals( " for update of t_.id", withAlias );

		assertEquals( " for update", dialect.getWriteLockString( Timeouts.SKIP_LOCKED ) );
		assertEquals( " for update of t_.id", dialect.getForUpdateSkipLockedString( "t_.id" ) );
	}

	// -------------------------------------------------------------------------
	// Behavioral — PESSIMISTIC_READ → exclusive FOR UPDATE (same shape as WRITE)
	// -------------------------------------------------------------------------

	@Test
	void pessimisticReadMatchesWriteLockShape_A_LCK_005() {
		assertEquals(
				dialect.getWriteLockString( Timeouts.WAIT_FOREVER ),
				dialect.getReadLockString( Timeouts.WAIT_FOREVER ),
				"PESSIMISTIC_READ must map to exclusive FOR UPDATE — " + USER_GUIDE_LOCK );

		assertEquals(
				dialect.getWriteLockString( "e_.id", Timeout.milliseconds( 1500 ) ),
				dialect.getReadLockString( "e_.id", Timeout.milliseconds( 1500 ) ),
				"read OF-column + WAIT ms must match write path" );

		assertEquals(
				dialect.getWriteLockString( Timeouts.NO_WAIT ),
				dialect.getReadLockString( Timeouts.NO_WAIT ),
				"read NOWAIT must match write NOWAIT" );
	}

	@Test
	void pessimisticReadLockOptions_sameSqlAsWrite_A_LCK_005() {
		LockOptions read = new LockOptions( LockMode.PESSIMISTIC_READ );
		LockOptions write = new LockOptions( LockMode.PESSIMISTIC_WRITE );
		String alias = "p_.id";

		assertEquals(
				dialect.getForUpdateString( alias, write ),
				dialect.getForUpdateString( alias, read ),
				"LockOptions PESSIMISTIC_READ must emit same FOR UPDATE fragment as WRITE" );
		assertTrue( dialect.getForUpdateString( alias, read ).toLowerCase( Locale.ROOT ).contains( "for update" ) );
		assertNoForbiddenLockKeywords( "PESSIMISTIC_READ LockOptions", dialect.getForUpdateString( alias, read ) );
	}

	@Test
	void limitHandlerWithReadLockFragment_preservesXuGuOrder_A_PAG_001() {
		Limit limit = new Limit( 2, 5 );
		String readWait = dialect.getReadLockString( Timeout.milliseconds( 500 ) ).trim();
		String base = "select id from t order by id " + readWait;
		String sql = limits.processSql( base, limit );

		assertNoForbiddenLockKeywords( "read lock + pagination", sql );
		assertTrue( sql.toLowerCase( Locale.ROOT ).contains( "for update" ) );
		int fuAt = sql.toLowerCase( Locale.ROOT ).indexOf( "for update" );
		int limAt = sql.toLowerCase( Locale.ROOT ).indexOf( "limit" );
		int waitAt = sql.toLowerCase( Locale.ROOT ).lastIndexOf( "wait" );
		assertTrue( fuAt >= 0 && limAt > fuAt && waitAt > limAt,
				"expected FOR UPDATE … LIMIT … WAIT: " + sql );
	}

	@Test
	void p005LockSemanticsChecklist_coversQ2Evidence() {
		lockingSupport_declaresSkipLockedFalse_A_LCK_004();
		skipLockedLockMode_stillEmitsPlainForUpdate_A_LCK_004();
		pessimisticReadMatchesWriteLockShape_A_LCK_005();
		pessimisticReadLockOptions_sameSqlAsWrite_A_LCK_005();
		limitHandlerWithReadLockFragment_preservesXuGuOrder_A_PAG_001();
		generatedLockSql_neverContainsSkipLockedOrForShare(
				"write WAIT_FOREVER", dialect.getWriteLockString( Timeouts.WAIT_FOREVER ) );
	}

	private static Stream<Arguments> allLockSqlFragments() {
		XuguDialect d = new XuguDialect();
		return Stream.of(
				Arguments.of( "getForUpdateString()", d.getForUpdateString() ),
				Arguments.of( "getForUpdateString(alias)", d.getForUpdateString( "t_.id" ) ),
				Arguments.of( "getForUpdateNowaitString()", d.getForUpdateNowaitString() ),
				Arguments.of( "getForUpdateNowaitString(alias)", d.getForUpdateNowaitString( "t_.id" ) ),
				Arguments.of( "getForUpdateSkipLockedString()", d.getForUpdateSkipLockedString() ),
				Arguments.of( "getForUpdateSkipLockedString(alias)", d.getForUpdateSkipLockedString( "t_.id" ) ),
				Arguments.of( "write WAIT_FOREVER", d.getWriteLockString( Timeouts.WAIT_FOREVER ) ),
				Arguments.of( "write NO_WAIT", d.getWriteLockString( Timeouts.NO_WAIT ) ),
				Arguments.of( "write SKIP_LOCKED magic", d.getWriteLockString( Timeouts.SKIP_LOCKED ) ),
				Arguments.of( "write WAIT ms", d.getWriteLockString( Timeout.milliseconds( 2000 ) ) ),
				Arguments.of( "write OF WAIT ms", d.getWriteLockString( "t_.id", Timeout.milliseconds( 2000 ) ) ),
				Arguments.of( "read WAIT_FOREVER", d.getReadLockString( Timeouts.WAIT_FOREVER ) ),
				Arguments.of( "read NO_WAIT", d.getReadLockString( Timeouts.NO_WAIT ) ),
				Arguments.of( "read WAIT ms", d.getReadLockString( Timeout.milliseconds( 2000 ) ) ),
				Arguments.of( "read OF WAIT ms", d.getReadLockString( "t_.id", Timeout.milliseconds( 2000 ) ) ),
				Arguments.of(
						"LockOptions PESSIMISTIC_READ",
						d.getForUpdateString( "e_.id", new LockOptions( LockMode.PESSIMISTIC_READ ) ) ),
				Arguments.of(
						"LockOptions PESSIMISTIC_WRITE",
						d.getForUpdateString( "e_.id", new LockOptions( LockMode.PESSIMISTIC_WRITE ) ) ),
				Arguments.of(
						"LockOptions UPGRADE_SKIPLOCKED",
						d.getForUpdateString( "e_.id", new LockOptions( LockMode.UPGRADE_SKIPLOCKED ) ) ) );
	}

	private static void assertNoForbiddenLockKeywords(String label, String sqlFragment) {
		String lower = sqlFragment.toLowerCase( Locale.ROOT );
		assertFalse( lower.contains( "skip locked" ),
				label + " must not emit SKIP LOCKED (A-LCK-004): " + sqlFragment );
		assertFalse( lower.contains( "for share" ),
				label + " must not emit FOR SHARE (A-LCK-005): " + sqlFragment );
		assertFalse( lower.contains( "for key share" ),
				label + " must not emit FOR KEY SHARE (A-LCK-005): " + sqlFragment );
	}
}
