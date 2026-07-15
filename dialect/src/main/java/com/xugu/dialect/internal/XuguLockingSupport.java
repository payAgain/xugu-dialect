package com.xugu.dialect.internal;

import org.hibernate.dialect.RowLockStrategy;
import org.hibernate.dialect.lock.PessimisticLockStyle;
import org.hibernate.dialect.lock.internal.LockingSupportParameterized;
import org.hibernate.dialect.lock.spi.LockingSupport;
import org.hibernate.dialect.lock.spi.OuterJoinLockingType;

/**
 * XuGu locking capability flags for Hibernate 7.4.
 *
 * <ul>
 *   <li>FOR UPDATE clause + OF columns ({@link RowLockStrategy#COLUMN})</li>
 *   <li>NOWAIT / WAIT ms supported (query-level)</li>
 *   <li>SKIP LOCKED <b>not</b> supported (A-LCK-004)</li>
 * </ul>
 */
public final class XuguLockingSupport {

	/**
	 * wait=true, nowait=true, skipLocked=false
	 */
	public static final LockingSupport INSTANCE = new LockingSupportParameterized(
			PessimisticLockStyle.CLAUSE,
			RowLockStrategy.COLUMN,
			true,
			true,
			false,
			OuterJoinLockingType.FULL
	);

	private XuguLockingSupport() {
	}
}
