package org.flywaydb.database.oracle;

import java.sql.SQLException;

/**
 * Avoid Oracle {@code SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')} — unsupported on Xugu (E19235).
 */
public class XuguConnection extends OracleConnection {

	XuguConnection(OracleDatabase database, java.sql.Connection connection) {
		super( database, connection );
	}

	@Override
	protected String getCurrentSchemaNameOrSearchPath() throws SQLException {
		// Matches Hibernate default catalog/schema on local demo (SYSTEM/SYSDBA).
		return "SYSDBA";
	}

	@Override
	public void doChangeCurrentSchemaOrSearchPathTo(String schema) {
		// Xugu demo uses single schema; skip Oracle ALTER SESSION SET CURRENT_SCHEMA.
	}

	@Override
	public org.flywaydb.core.internal.database.base.Schema getSchema(String name) {
		return new XuguSchema( getJdbcTemplate(), (OracleDatabase) database, name );
	}
}
