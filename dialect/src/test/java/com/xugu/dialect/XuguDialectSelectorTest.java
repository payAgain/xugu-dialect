package com.xugu.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for {@link XuguDialectSelector} closure (C-SEL-001).
 */
class XuguDialectSelectorTest {

	@Test
	void defaultSelectorReturnsSpiDialect_C_SEL_001() {
		DialectResolutionInfo info = XuguDialectResolverTestSupport.fake(
				"XuguDB", "XuguDB JDBC Driver", 12, 3 );
		XuguDialect dialect = XuguDialectSelector.Default.INSTANCE.selectDialect( info );
		assertNotNull( dialect );
		assertNotNull( dialect.getServerConfiguration() );
		assertEquals( 12, dialect.getVersion().getMajor() );
		assertEquals( 3, dialect.getVersion().getMinor() );
	}

	@Test
	void resolverUsesDefaultSelector_C_SEL_001() {
		XuguDialectResolver resolver = new XuguDialectResolver();
		DialectResolutionInfo info = XuguDialectResolverTestSupport.fake(
				"XuguDB", "XuguDB JDBC Driver", 12, 0 );
		Dialect dialect = resolver.resolveDialect( info );
		assertInstanceOf( XuguDialect.class, dialect );
	}

	@Test
	void customSelectorCanBeInjected_C_SEL_001() {
		XuguDialectSelector countingSelector = info -> new XuguDialect( info.makeCopyOrDefault(
				XuguDialect.MINIMUM_VERSION ) );
		XuguDialectResolver resolver = new XuguDialectResolver( countingSelector );
		Dialect dialect = resolver.resolveDialect(
				XuguDialectResolverTestSupport.fake( "XuguDB", null, 11, 2 ) );
		assertInstanceOf( XuguDialect.class, dialect );
	}
}
