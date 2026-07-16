package com.xugu.dialect.exception;

/**
 * XuguDB vendor error codes used for Hibernate JDBC exception conversion.
 *
 * <p>Codes from {@code E:\Work\docs\content\reference\error-code\}:
 * e13.md (E13001/E13005/E13008), e14.md (E14001/E14011/E14012), e16.md (E16005), e19.md (E19013).
 * JDBC often exposes these via {@code SQLException#getErrorCode()} or {@code SQLState=xuguNNNNN}.
 */
public final class XuguErrorCodes {

	/** E13001 — unique constraint violation */
	public static final int UNIQUE_VIOLATION = 13_001;

	/** E13005 — foreign key violation */
	public static final int FOREIGN_KEY_VIOLATION = 13_005;

	/** E13008 — check constraint violation */
	public static final int CHECK_VIOLATION = 13_008;

	/** E16005 — column cannot be null */
	public static final int NOT_NULL_VIOLATION = 16_005;

	/** E14001 — deadlock; transaction rolled back */
	public static final int DEADLOCK = 14_001;

	/** E14011 — resource busy (lock timeout) */
	public static final int LOCK_TIMEOUT = 14_011;

	/** E14012 — resource busy (named lock timeout) */
	public static final int LOCK_TIMEOUT_DETAIL = 14_012;

	/** E19013 — lock upgrade conflict or timeout */
	public static final int LOCK_UPGRADE_CONFLICT = 19_013;

	private XuguErrorCodes() {
	}
}
