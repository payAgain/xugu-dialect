package com.xugu.dialect.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * JDBC connection defaults for P-003 IT (compatible_mode=NONE).
 */
public final class XuguTestConnection {

	public static final String DRIVER = "com.xugu.cloudjdbc.Driver";
	public static final String DEFAULT_URL =
			"jdbc:xugu://127.0.0.1:5138/SYSTEM?user=SYSDBA&password=SYSDBA&compatiblemode=NONE";

	private XuguTestConnection() {
	}

	public static String jdbcUrl() {
		String url = System.getenv( "XUGU_JDBC_URL" );
		if ( url == null || url.isBlank() ) {
			url = DEFAULT_URL;
		}
		String user = System.getenv( "XUGU_USER" );
		String password = System.getenv( "XUGU_PASSWORD" );
		if ( user != null && !user.isBlank() && !url.contains( "user=" ) ) {
			url = appendParam( url, "user", user );
		}
		if ( password != null && !password.isBlank() && !url.contains( "password=" ) ) {
			url = appendParam( url, "password", password );
		}
		if ( !url.toLowerCase().contains( "compatiblemode=" ) ) {
			url = appendParam( url, "compatiblemode", "NONE" );
		}
		return url;
	}

	public static Connection open() throws SQLException {
		try {
			Class.forName( DRIVER );
		}
		catch ( ClassNotFoundException e ) {
			throw new SQLException( "Xugu JDBC driver not on classpath: " + DRIVER, e );
		}
		Properties props = new Properties();
		String user = System.getenv( "XUGU_USER" );
		String password = System.getenv( "XUGU_PASSWORD" );
		if ( user != null && !user.isBlank() ) {
			props.setProperty( "user", user );
		}
		if ( password != null && !password.isBlank() ) {
			props.setProperty( "password", password );
		}
		if ( props.isEmpty() ) {
			return DriverManager.getConnection( jdbcUrl() );
		}
		return DriverManager.getConnection( jdbcUrl(), props );
	}

	private static String appendParam(String url, String key, String value) {
		return url + ( url.contains( "?" ) ? "&" : "?" ) + key + "=" + value;
	}
}
