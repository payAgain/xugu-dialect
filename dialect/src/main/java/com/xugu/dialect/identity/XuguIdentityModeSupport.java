package com.xugu.dialect.identity;

/**
 * XuGu session {@code IDENTITY_MODE} parameter ({@code reference/system-configuration-parameter/session-parameter/identity_mode.md},
 * {@code reference/system-configuration-parameter/xugu.ini/compatible/def_identity_mode.md}).
 *
 * <p><b>A-IDN-005:</b> Controls NULL/ZERO-as-auto-increment behaviour for explicit INSERT values
 * (v12.0.6+). Complements {@link XuguIdentityColumnSupport} DDL ({@code identity(1,1)}).
 */
public final class XuguIdentityModeSupport {

	/**
	 * Documented session values ({@code identity_mode.md} table 2).
	 */
	public enum IdentityMode {
		DEFAULT( "DEFAULT" ),
		DEFAULT_IDENTITY_MODE( "DEFAULT_IDENTITY_MODE" ),
		NULL_AS_AUTO_INCREMENT( "NULL_AS_AUTO_INCREMENT" ),
		ZERO_AS_AUTO_INCREMENT( "ZERO_AS_AUTO_INCREMENT" );

		private final String sessionValue;

		IdentityMode(String sessionValue) {
			this.sessionValue = sessionValue;
		}

		public String sessionValue() {
			return sessionValue;
		}
	}

	/** {@code SHOW IDENTITY_MODE} ({@code identity_mode.md} example 1). */
	public static final String SHOW_IDENTITY_MODE_QUERY = "show identity_mode";

	private XuguIdentityModeSupport() {
	}

	public static IdentityMode[] documentedIdentityModes() {
		return IdentityMode.values();
	}

	/**
	 * {@code SET IDENTITY_MODE TO mode} ({@code identity_mode.md} example 2).
	 */
	public static String setIdentityModeSql(IdentityMode mode) {
		return "set identity_mode to " + mode.sessionValue();
	}

	/**
	 * {@code ALTER SESSION SET IDENTITY_MODE = mode} ({@code identity_mode.md} example 2 alternate).
	 */
	public static String alterSessionSetIdentityModeSql(IdentityMode mode) {
		return "alter session set identity_mode = " + mode.sessionValue();
	}
}
