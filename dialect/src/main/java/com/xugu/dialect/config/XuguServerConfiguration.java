package com.xugu.dialect.config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

/**
 * Read-only XuGu session / JDBC metadata probe (C-SRV-001).
 *
 * <p>Docs: {@code reference/system-configuration-parameter/session-parameter/*.md}
 * (30 session params). Probes use documented {@code SHOW} only — no mutating {@code SET}.
 *
 * <p>MySQL {@code @@sql_mode} has no XuGu equivalent; see {@link #supportsSqlMode()}.
 */
public final class XuguServerConfiguration {

	/** Documented probe: {@code compatible_mode.md} §示例. */
	public static final String SHOW_COMPATIBLE_MODE = "SHOW COMPATIBLE_MODE";

	/** Documented probe: {@code char_set.md} §示例. */
	public static final String SHOW_CHAR_SET = "SHOW CHAR_SET";

	/** Documented probe: {@code optimizer_mode.md} §示例. */
	public static final String SHOW_OPTIMIZER_MODE = "SHOW OPTIMIZER_MODE";

	private final String databaseProductName;
	private final String databaseProductVersion;
	private final String driverName;
	private final String driverVersion;
	private final int majorVersion;
	private final int minorVersion;
	private final String charSet;
	private final String compatibleMode;
	private final String optimizerMode;

	public XuguServerConfiguration(
			String databaseProductName,
			String databaseProductVersion,
			String driverName,
			String driverVersion,
			int majorVersion,
			int minorVersion) {
		this( databaseProductName, databaseProductVersion, driverName, driverVersion,
				majorVersion, minorVersion, null, null, null );
	}

	public XuguServerConfiguration(
			String databaseProductName,
			String databaseProductVersion,
			String driverName,
			String driverVersion,
			int majorVersion,
			int minorVersion,
			String charSet,
			String compatibleMode,
			String optimizerMode) {
		this.databaseProductName = databaseProductName;
		this.databaseProductVersion = databaseProductVersion;
		this.driverName = driverName;
		this.driverVersion = driverVersion;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.charSet = charSet;
		this.compatibleMode = compatibleMode;
		this.optimizerMode = optimizerMode;
	}

	/**
	 * XuGu has no MySQL {@code sql_mode} session variable.
	 */
	public static boolean supportsSqlMode() {
		return false;
	}

	public String getDatabaseProductName() {
		return databaseProductName;
	}

	public String getDatabaseProductVersion() {
		return databaseProductVersion;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public String getCharSet() {
		return charSet;
	}

	public String getCompatibleMode() {
		return compatibleMode;
	}

	public String getOptimizerMode() {
		return optimizerMode;
	}

	public boolean isMysqlCompatibleMode() {
		return compatibleMode != null && "MYSQL".equalsIgnoreCase( compatibleMode.trim() );
	}

	/**
	 * Build from Hibernate dialect resolution info; falls back to resolution fields when JDBC
	 * metadata is unavailable (offline unit tests).
	 */
	public static XuguServerConfiguration fromDialectResolutionInfo(DialectResolutionInfo info) {
		final DatabaseMetaData databaseMetaData = info.getDatabaseMetadata();
		if ( databaseMetaData != null ) {
			return fromDatabaseMetadata( databaseMetaData );
		}
		return new XuguServerConfiguration(
				info.getDatabaseName(),
				info.getDatabaseVersion(),
				info.getDriverName(),
				formatDriverVersion( info ),
				Math.max( info.getDatabaseMajorVersion(), 0 ),
				Math.max( info.getDatabaseMinorVersion(), 0 )
		);
	}

	/**
	 * Read-only session probes via documented {@code SHOW}; URL query fallback when SHOW fails.
	 */
	public static XuguServerConfiguration fromDatabaseMetadata(DatabaseMetaData databaseMetaData) {
		try {
			String charSet = null;
			String compatibleMode = null;
			String optimizerMode = null;
			String jdbcUrl = null;
			try {
				jdbcUrl = databaseMetaData.getURL();
			}
			catch ( SQLException ignored ) {
				// fall through
			}
			try {
				Connection connection = databaseMetaData.getConnection();
				if ( connection != null ) {
					charSet = querySessionVariable( connection, SHOW_CHAR_SET );
					compatibleMode = querySessionVariable( connection, SHOW_COMPATIBLE_MODE );
					optimizerMode = querySessionVariable( connection, SHOW_OPTIMIZER_MODE );
				}
			}
			catch ( SQLException ignored ) {
				// fall through to JDBC URL fallback
			}
			if ( charSet == null ) {
				charSet = readUrlQueryParam( jdbcUrl, "charset" );
			}
			if ( compatibleMode == null ) {
				compatibleMode = readUrlQueryParam( jdbcUrl, "compatiblemode" );
			}
			return new XuguServerConfiguration(
					databaseMetaData.getDatabaseProductName(),
					databaseMetaData.getDatabaseProductVersion(),
					databaseMetaData.getDriverName(),
					databaseMetaData.getDriverVersion(),
					interpretVersion( databaseMetaData.getDatabaseMajorVersion() ),
					interpretVersion( databaseMetaData.getDatabaseMinorVersion() ),
					charSet,
					compatibleMode,
					optimizerMode
			);
		}
		catch ( SQLException ex ) {
			return new XuguServerConfiguration( null, null, null, null, 0, 0 );
		}
	}

	public static String querySessionVariable(Connection connection, String showSql) throws SQLException {
		try ( Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery( showSql ) ) {
			if ( resultSet.next() ) {
				return resultSet.getString( 1 );
			}
		}
		return null;
	}

	public static String readUrlQueryParam(String jdbcUrl, String paramName) {
		if ( jdbcUrl == null || paramName == null || paramName.isBlank() ) {
			return null;
		}
		int queryStart = jdbcUrl.indexOf( '?' );
		if ( queryStart < 0 || queryStart == jdbcUrl.length() - 1 ) {
			return null;
		}
		String query = jdbcUrl.substring( queryStart + 1 );
		String prefix = paramName.toLowerCase( Locale.ROOT ) + '=';
		for ( String part : query.split( "&" ) ) {
			if ( part.regionMatches( true, 0, prefix, 0, prefix.length() ) ) {
				String value = part.substring( prefix.length() );
				return value.isBlank() ? null : value;
			}
		}
		return null;
	}

	private static String formatDriverVersion(DialectResolutionInfo info) {
		if ( info.getDriverMajorVersion() <= 0 && info.getDriverMinorVersion() <= 0 ) {
			return null;
		}
		return info.getDriverMajorVersion() + "." + info.getDriverMinorVersion();
	}

	private static int interpretVersion(int version) {
		return version < 0 ? 0 : version;
	}
}
