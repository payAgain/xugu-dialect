package com.xugu.dialect.support;

/**
 * Gate for live-XuguDB integration tests.
 * Enabled when {@code -Dxugu.run.integration=true} or env {@code XUGU_RUN_IT=true}.
 */
public final class XuguITGate {

	public static final String SYSTEM_PROPERTY = "xugu.run.integration";
	public static final String ENV_VAR = "XUGU_RUN_IT";

	private XuguITGate() {
	}

	public static boolean isEnabled() {
		return "true".equalsIgnoreCase( System.getProperty( SYSTEM_PROPERTY ) )
				|| "true".equalsIgnoreCase( System.getenv( ENV_VAR ) );
	}
}
