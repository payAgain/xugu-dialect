package com.xugu.dialect;

import org.junit.jupiter.api.Test;

import com.xugu.dialect.config.XuguServerConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: C-JSON-006 doc-forbidden + C-SRV-001 session probe constants.
 */
class XuguRulerCClosureTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void jsonTableNotSupported_C_JSON_006() {
		assertFalse( dialect.supportsJsonTableFunction(),
				"C-JSON-006 doc-forbidden — no json_table under reference/function/json-functions/**" );
	}

	@Test
	void serverConfigurationShowQueriesLocked_C_SRV_001() {
		assertEquals( "SHOW COMPATIBLE_MODE", XuguServerConfiguration.SHOW_COMPATIBLE_MODE );
		assertEquals( "SHOW CHAR_SET", XuguServerConfiguration.SHOW_CHAR_SET );
		assertEquals( "SHOW OPTIMIZER_MODE", XuguServerConfiguration.SHOW_OPTIMIZER_MODE );
	}

	@Test
	void serverConfigurationNoSqlMode_C_SRV_001() {
		assertFalse( XuguServerConfiguration.supportsSqlMode(),
				"XuGu has no MySQL sql_mode session variable" );
	}

	@Test
	void serverConfigurationUrlFallback_C_SRV_001() {
		String url = "jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE&charset=utf8";
		assertEquals( "NONE", XuguServerConfiguration.readUrlQueryParam( url, "compatiblemode" ) );
		assertEquals( "utf8", XuguServerConfiguration.readUrlQueryParam( url, "charset" ) );
		assertNull( XuguServerConfiguration.readUrlQueryParam( url, "missing" ) );
	}

	@Test
	void versionOnlyDialectHasNoServerConfiguration_C_SRV_001() {
		assertNull( dialect.getServerConfiguration(),
				"version-only ctor has no JDBC metadata probe" );
	}

	@Test
	void spiDialectExposesServerConfigurationFromResolutionInfo_C_SRV_001() {
		XuguDialect spiDialect = new XuguDialect(
				XuguDialectResolverTestSupport.fake( "XuguDB", "XuguDB JDBC Driver", 12, 0 ) );
		XuguServerConfiguration config = spiDialect.getServerConfiguration();
		assertNotNull( config );
		assertEquals( "XuguDB", config.getDatabaseProductName() );
		assertEquals( 12, config.getMajorVersion() );
		assertTrue( config.getCompatibleMode() == null || !config.getCompatibleMode().isBlank()
				|| config.getCharSet() == null,
				"offline resolution info has no JDBC SHOW probes" );
	}
}
