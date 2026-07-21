package com.xugu.dialect;

import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

import java.util.Map;

/**
 * Shared fake {@link DialectResolutionInfo} for resolver / selector / server-config unit tests.
 */
final class XuguDialectResolverTestSupport {

	private XuguDialectResolverTestSupport() {
	}

	static DialectResolutionInfo fake(String product, String driver, int major, int minor) {
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
