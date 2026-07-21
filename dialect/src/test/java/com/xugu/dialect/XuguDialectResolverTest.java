package com.xugu.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for {@link XuguDialectResolver} match / non-match (A-SPI-003/004).
 */
class XuguDialectResolverTest {

	private final XuguDialectResolver resolver = new XuguDialectResolver();

	@Test
	void resolvesXuguProductName_withDatabaseVersion() {
		DialectResolutionInfo info = fake( "XuguDB", "XuguDB JDBC Driver", 12, 0 );
		Dialect dialect = resolver.resolveDialect( info );
		assertInstanceOf( XuguDialect.class, dialect );
		assertEquals( 12, dialect.getVersion().getMajor() );
		assertEquals( 0, dialect.getVersion().getMinor() );
	}

	@Test
	void resolvesWhenOnlyDriverNameMatches() {
		Dialect dialect = resolver.resolveDialect( fake( "UnknownDB", "XuguDB JDBC Driver", 11, 2 ) );
		assertInstanceOf( XuguDialect.class, dialect );
		assertEquals( 11, dialect.getVersion().getMajor() );
		assertEquals( 2, dialect.getVersion().getMinor() );
	}

	@Test
	void resolvesCaseInsensitiveProductToken() {
		Dialect dialect = resolver.resolveDialect( fake( "xugudb", null, 12, 1 ) );
		assertInstanceOf( XuguDialect.class, dialect );
		assertEquals( 12, dialect.getVersion().getMajor() );
		assertEquals( 1, dialect.getVersion().getMinor() );
	}

	@Test
	void returnsNullForMySQL() {
		assertNull( resolver.resolveDialect( fake( "MySQL", "MySQL Connector/J", 8, 0 ) ) );
	}

	@Test
	void returnsNullForOracle() {
		assertNull( resolver.resolveDialect( fake( "Oracle", "Oracle JDBC driver", 19, 0 ) ) );
	}

	@Test
	void returnsNullForPostgreSQL() {
		assertNull( resolver.resolveDialect( fake( "PostgreSQL", "PostgreSQL JDBC Driver", 16, 0 ) ) );
	}

	@Test
	void returnsNullWhenNamesBlank() {
		assertNull( resolver.resolveDialect( fake( null, null, 0, 0 ) ) );
		assertNull( resolver.resolveDialect( fake( "", "  ", 0, 0 ) ) );
	}

	@Test
	void matchesHelper_productAndDriver() {
		assertTrue( XuguDialectResolver.matchesXugu( fake( "XuguDB", null, 12, 0 ) ) );
		assertTrue( XuguDialectResolver.matchesXugu( fake( null, "XuguDB JDBC Driver", 12, 0 ) ) );
	}

	private static DialectResolutionInfo fake(String product, String driver, int major, int minor) {
		return XuguDialectResolverTestSupport.fake( product, driver, major, minor );
	}
}
