package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.config.XuguServerConfiguration;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gated IT: C-SRV-001 read-only session parameter probes via documented {@code SHOW}.
 */
class XuguServerConfigurationIT {

	@Test
	@EnabledIf( "com.xugu.dialect.support.XuguITGate#isEnabled" )
	void sessionParametersReadOnlyProbe_C_SRV_001() {
		try ( Connection connection = XuguTestConnection.open() ) {
			DatabaseMetaData meta = connection.getMetaData();
			XuguServerConfiguration config = XuguServerConfiguration.fromDatabaseMetadata( meta );

			assertNotNull( config.getDatabaseProductName() );
			assertTrue( config.getDatabaseProductName().toLowerCase().contains( "xugu" ),
					"product name should identify XuguDB" );

			String compatibleMode = config.getCompatibleMode();
			if ( compatibleMode == null ) {
				compatibleMode = XuguServerConfiguration.querySessionVariable(
						connection, XuguServerConfiguration.SHOW_COMPATIBLE_MODE );
			}
			assertNotNull( compatibleMode, "SHOW COMPATIBLE_MODE should return a value on live DB" );
			assertEquals( "NONE", compatibleMode.trim().toUpperCase(),
					"integration gate requires compatiblemode=NONE" );

			String charSet = config.getCharSet();
			if ( charSet == null ) {
				charSet = XuguServerConfiguration.querySessionVariable(
						connection, XuguServerConfiguration.SHOW_CHAR_SET );
			}
			assertNotNull( charSet, "SHOW CHAR_SET should return a value on live DB" );
			assertFalse( charSet.isBlank() );

			XuguDialect dialect = new XuguDialect(
					new XuguDialectResolutionInfo( meta ) );
			assertNotNull( dialect.getServerConfiguration() );
			assertEquals( compatibleMode, dialect.getServerConfiguration().getCompatibleMode() );
		}
		catch ( Exception e ) {
			throw new AssertionError( "C-SRV-001 server configuration IT failed: " + e.getMessage(), e );
		}
	}

	/**
	 * Minimal {@link org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo} wrapper for IT.
	 */
	private static final class XuguDialectResolutionInfo
			implements org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo {
		private final DatabaseMetaData meta;

		XuguDialectResolutionInfo(DatabaseMetaData meta) {
			this.meta = meta;
		}

		@Override
		public String getDatabaseName() {
			try {
				return meta.getDatabaseProductName();
			}
			catch ( Exception e ) {
				return null;
			}
		}

		@Override
		public String getDatabaseVersion() {
			try {
				return meta.getDatabaseProductVersion();
			}
			catch ( Exception e ) {
				return null;
			}
		}

		@Override
		public String getDriverName() {
			try {
				return meta.getDriverName();
			}
			catch ( Exception e ) {
				return null;
			}
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
			try {
				return meta.getDatabaseMajorVersion();
			}
			catch ( Exception e ) {
				return 0;
			}
		}

		@Override
		public int getDatabaseMinorVersion() {
			try {
				return meta.getDatabaseMinorVersion();
			}
			catch ( Exception e ) {
				return 0;
			}
		}

		@Override
		public java.util.Map<String, Object> getConfigurationValues() {
			return java.util.Map.of();
		}

		@Override
		public DatabaseMetaData getDatabaseMetadata() {
			return meta;
		}
	}
}
