package com.xugu.demo.support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * JDBC helpers for gated demo IT (same env keys as {@code application.yml}).
 */
public final class DemoXuguJdbc {

	public static final String DRIVER = "com.xugu.cloudjdbc.Driver";
	public static final String DEFAULT_URL =
			"jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE";
	public static final String PERSON_TABLE = "HIB_DEMO_PERSON";

	private DemoXuguJdbc() {
	}

	public static String jdbcUrl() {
		String url = System.getenv( "XUGU_JDBC_URL" );
		if ( url == null || url.isBlank() ) {
			url = DEFAULT_URL;
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
		else {
			props.setProperty( "user", "SYSDBA" );
		}
		if ( password != null && !password.isBlank() ) {
			props.setProperty( "password", password );
		}
		else {
			props.setProperty( "password", "SYSDBA" );
		}
		return DriverManager.getConnection( jdbcUrl(), props );
	}

	/**
	 * Pre-create {@code HIB_DEMO_PERSON} so Boot can start with {@code ddl-auto=validate}.
	 * DDL matches Hibernate export for {@link com.xugu.demo.entity.DemoPerson} (IDENTITY).
	 */
	public static void ensurePersonTable() throws SQLException {
		try ( Connection c = open(); Statement st = c.createStatement() ) {
			st.execute(
					"CREATE TABLE IF NOT EXISTS " + PERSON_TABLE
							+ " (id bigint identity(1,1), name varchar(128) not null, primary key (id))" );
		}
	}

	public static boolean personTableExists() throws SQLException {
		try ( Connection c = open(); Statement st = c.createStatement() ) {
			st.executeQuery( "SELECT 1 FROM " + PERSON_TABLE + " WHERE 1=0" );
			return true;
		}
		catch ( SQLException e ) {
			return false;
		}
	}

	private static String appendParam(String url, String key, String value) {
		return url + ( url.contains( "?" ) ? "&" : "?" ) + key + "=" + value;
	}
}
