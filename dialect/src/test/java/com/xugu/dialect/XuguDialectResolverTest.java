package com.xugu.dialect;

import java.util.Map;

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
		return new FakeResolutionInfo( product, driver, major, minor );
	}

	private static final class FakeResolutionInfo implements DialectResolutionInfo {
		private final String product;
		private final String driver;
		private final int major;
		private final int minor;

		FakeResolutionInfo(String product, String driver, int major, int minor) {
			this.product = product;
			this.driver = driver;
			this.major = major;
			this.minor = minor;
		}

		@Override
		public String getDatabaseName() {
			return product;
		}

		@Override
		public String getDatabaseVersion() {
			return major + "." + minor;
		}

		@Override
		public String getDriverName() {
			return driver;
		}

		@Override
		public int getDriverMajorVersion() {
			return 0;
		}

		@Override
		public int getDriverMinorVersion() {
			return 0;
		}

		@Override
		public String getSQLKeywords() {
			return "";
		}

		@Override
		public int getDatabaseMajorVersion() {
			return major;
		}

		@Override
		public int getDatabaseMinorVersion() {
			return minor;
		}

		@Override
		public Map<String, Object> getConfigurationValues() {
			return Map.of();
		}
	}
}
