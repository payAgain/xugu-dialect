package com.xugu.dialect.metadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * XuGu catalog (database) JDBC metadata alignment ({@code reference/object/database.md},
 * session {@code reference/system-configuration-parameter/session-parameter/database.md}).
 *
 * <p><b>A-SCH-003 known-limit:</b> XuGu {@code DATABASE} is connection-scoped and
 * <em>not</em> session-{@code SET}-able. Hibernate object names stay
 * {@code schema.table} ({@link org.hibernate.engine.jdbc.env.spi.NameQualifierSupport#SCHEMA});
 * JDBC {@link java.sql.DatabaseMetaData#getCatalog()} should align with {@code current_db()}
 * on the same connection, but {@code catalog.schema.table} qualification is not emitted.
 */
public final class XuguCatalogMetadataSupport {

	/** Documented {@code current_db()} ({@code database.md} §切换数据库). */
	public static final String CURRENT_CATALOG_QUERY = "select current_db()";

	private XuguCatalogMetadataSupport() {
	}

	/**
	 * Resolve the connection's current XuGu database name via documented {@code current_db()}.
	 */
	public static String queryCurrentDatabase(Connection connection) throws SQLException {
		try ( Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery( CURRENT_CATALOG_QUERY ) ) {
			if ( rs.next() ) {
				return rs.getString( 1 );
			}
		}
		return null;
	}

	/**
	 * Under {@code compatiblemode=NONE}, JDBC catalog metadata should match {@code current_db()}.
	 * When the driver returns an empty catalog, only {@code current_db()} non-empty is required.
	 */
	public static boolean jdbcCatalogAlignsWithCurrentDatabase(Connection connection) throws SQLException {
		final String currentDb = queryCurrentDatabase( connection );
		if ( currentDb == null || currentDb.isBlank() ) {
			return false;
		}
		final String jdbcCatalog = connection.getCatalog();
		if ( jdbcCatalog == null || jdbcCatalog.isBlank() ) {
			return true;
		}
		return jdbcCatalog.equalsIgnoreCase( currentDb );
	}
}
