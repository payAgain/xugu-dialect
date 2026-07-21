package com.xugu.dialect.lock;

/**
 * XuGu explicit {@code LOCK TABLE} helpers ({@code reference/object/table/lock.md}).
 *
 * <p><b>A-LCK-006:</b> Table-level locks are native SQL only — not wired to JPA
 * {@code LockMode} / {@code FOR UPDATE} ({@link com.xugu.dialect.internal.XuguLockingSupport}).
 * Distinct from doc-forbidden SELECT {@code SKIP LOCKED} / {@code FOR SHARE}.
 */
public final class XuguLockTableSupport {

	/**
	 * Documented table lock modes ({@code lock.md} {@code opt_lock}).
	 */
	public enum TableLockMode {
		/** Default X lock when {@code IN … MODE} is omitted. */
		DEFAULT( null ),
		SHARE( "share mode" ),
		EXCLUSIVE( "exclusive mode" ),
		ROW_SHARE( "row share mode" ),
		ROW_EXCLUSIVE( "row exclusive mode" );

		private final String sqlFragment;

		TableLockMode(String sqlFragment) {
			this.sqlFragment = sqlFragment;
		}
	}

	/**
	 * Documented wait strategies ({@code lock.md} {@code opt_wait}).
	 */
	public enum TableLockWait {
		/** Infinite wait (default when omitted). */
		NONE,
		NOWAIT,
		/** Explicit {@code WAIT} until lock available. */
		WAIT,
		/** {@code WAIT ms} — documented minimum 10 ms. */
		WAIT_MS
	}

	private XuguLockTableSupport() {
	}

	public static TableLockMode[] documentedLockModes() {
		return TableLockMode.values();
	}

	/**
	 * {@code LOCK TABLE name [IN mode] [NOWAIT|WAIT|WAIT ms]} ({@code lock.md}).
	 */
	public static String lockTableSql(String tableName, TableLockMode mode, TableLockWait wait) {
		return lockTableSql( new String[] { tableName }, mode, wait, null );
	}

	/**
	 * {@code LOCK TABLE t1, t2 … IN mode [wait]} for multi-table lock.
	 */
	public static String lockTableSql(String[] tableNames, TableLockMode mode, TableLockWait wait) {
		return lockTableSql( tableNames, mode, wait, null );
	}

	/**
	 * {@code LOCK TABLE … WAIT ms} with explicit millisecond timeout.
	 */
	public static String lockTableSql(
			String tableName,
			TableLockMode mode,
			TableLockWait wait,
			Integer waitMilliseconds) {
		return lockTableSql( new String[] { tableName }, mode, wait, waitMilliseconds );
	}

	public static String lockTableSql(
			String[] tableNames,
			TableLockMode mode,
			TableLockWait wait,
			Integer waitMilliseconds) {
		if ( tableNames == null || tableNames.length == 0 ) {
			throw new IllegalArgumentException( "tableNames required" );
		}
		final StringBuilder sb = new StringBuilder( "lock table " );
		sb.append( String.join( ", ", tableNames ) );
		if ( mode != null && mode != TableLockMode.DEFAULT && mode.sqlFragment != null ) {
			sb.append( " in " ).append( mode.sqlFragment );
		}
		if ( wait == TableLockWait.NOWAIT ) {
			sb.append( " nowait" );
		}
		else if ( wait == TableLockWait.WAIT ) {
			sb.append( " wait" );
		}
		else if ( wait == TableLockWait.WAIT_MS ) {
			if ( waitMilliseconds == null ) {
				throw new IllegalArgumentException( "waitMilliseconds required for WAIT_MS" );
			}
			sb.append( " wait " ).append( waitMilliseconds );
		}
		return sb.toString();
	}
}
