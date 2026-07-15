package com.xugu.demo.support;

/**
 * Gate for live-XuguDB Spring Boot IT.
 * Enabled when {@code -Dxugu.run.integration=true} or env {@code XUGU_RUN_IT=true}.
 */
public final class XuguIntegrationGate {

	public static final String SYSTEM_PROPERTY = "xugu.run.integration";
	public static final String ENV_VAR = "XUGU_RUN_IT";

	private XuguIntegrationGate() {
	}

	/** Used by {@code @EnabledIf} SpEL / method reference. */
	public static boolean isEnabled() {
		return "true".equalsIgnoreCase( System.getProperty( SYSTEM_PROPERTY ) )
				|| "true".equalsIgnoreCase( System.getenv( ENV_VAR ) );
	}
}
