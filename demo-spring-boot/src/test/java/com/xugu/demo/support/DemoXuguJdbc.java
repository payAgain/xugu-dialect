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
	public static final String DEPT_TABLE = "HIB_DEMO_DEPT";
	public static final String DEPT_MEMBER_TABLE = "HIB_DEMO_DEPT_MEMBER";
	public static final String SEQ_TICKET_TABLE = "HIB_DEMO_SEQ_TICKET";
	public static final String SEQ_TICKET_SEQUENCE = "HIB_DEMO_SEQ_TICKET_SEQ";
	public static final String TYPED_SAMPLE_TABLE = "HIB_DEMO_TYPED_SAMPLE";
	public static final String JSON_DOC_TABLE = "HIB_DEMO_JSON_DOC";

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

	/**
	 * Pre-create Layer A + Layer B tables/sequence for {@code ddl-auto=validate} startup
	 * (DemoValidateStartupIT). Matches Hibernate export for demo entities.
	 */
	public static void ensureValidateSchema() throws SQLException {
		ensurePersonTable();
		try ( Connection c = open(); Statement st = c.createStatement() ) {
			st.execute(
					"CREATE TABLE IF NOT EXISTS " + DEPT_TABLE
							+ " (id bigint identity(1,1), name varchar(128) not null, primary key (id))" );
			st.execute(
					"CREATE TABLE IF NOT EXISTS " + DEPT_MEMBER_TABLE
							+ " (id bigint identity(1,1), code varchar(64) not null, name varchar(128) not null,"
							+ " dept_id bigint not null,"
							+ " primary key (id),"
							+ " constraint UK_HIB_DEMO_DEPT_MEMBER_CODE unique (code),"
							+ " constraint FK_HIB_DEMO_DEPT_MEMBER_DEPT foreign key (dept_id)"
							+ " references " + DEPT_TABLE + " (id))" );
			ignore( st, "CREATE SEQUENCE IF NOT EXISTS " + SEQ_TICKET_SEQUENCE
					+ " START WITH 1 INCREMENT BY 1" );
			st.execute(
					"CREATE TABLE IF NOT EXISTS " + SEQ_TICKET_TABLE
							+ " (id bigint not null, label varchar(128) not null, primary key (id))" );
			recreateTypedSampleTable( st );
			st.execute(
					"CREATE TABLE IF NOT EXISTS " + JSON_DOC_TABLE
							+ " (id bigint identity(1,1),"
							+ " label varchar(64) not null,"
							+ " payload json,"
							+ " primary key (id))" );
		}
	}

	/**
	 * Recreate typed-sample table with {@code guid_val varchar(36)} (A-TYP-012 Boot mapping).
	 * Drops prior {@code guid}-typed columns from earlier P-004 drafts so validate/update stay aligned.
	 */
	public static void recreateTypedSampleTable() throws SQLException {
		try ( Connection c = open(); Statement st = c.createStatement() ) {
			recreateTypedSampleTable( st );
		}
	}

	private static void recreateTypedSampleTable(Statement st) throws SQLException {
		ignore( st, "DROP TABLE IF EXISTS " + TYPED_SAMPLE_TABLE );
		st.execute(
				"CREATE TABLE IF NOT EXISTS " + TYPED_SAMPLE_TABLE
						+ " (id bigint identity(1,1),"
						+ " int_val integer not null,"
						+ " dec_val numeric(12,2) not null,"
						+ " label varchar(64) not null,"
						+ " flag_val boolean not null,"
						+ " day_val date not null,"
						+ " ts_val timestamp(3) not null,"
						+ " bin_val binary,"
						+ " blob_val blob,"
						+ " guid_val varchar(36),"
						+ " primary key (id))" );
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

	private static void ignore(Statement st, String sql) {
		try {
			st.execute( sql );
		}
		catch ( SQLException ignored ) {
			// XuGu may lack IF NOT EXISTS on SEQUENCE; validate path tolerates pre-existing seq
		}
	}

	private static String appendParam(String url, String key, String value) {
		return url + ( url.contains( "?" ) ? "&" : "?" ) + key + "=" + value;
	}
}
