package org.flywaydb.database.oracle;

import org.flywaydb.core.internal.jdbc.JdbcTemplate;

/**
 * Xugu demo schema handle — skip Oracle ALL_USERS probe (USERNAME column absent).
 */
public class XuguSchema extends OracleSchema {

	XuguSchema(JdbcTemplate jdbcTemplate, OracleDatabase database, String name) {
		super( jdbcTemplate, database, name );
	}

	@Override
	protected boolean doExists() {
		return true;
	}

	@Override
	protected boolean doEmpty() {
		// Skip Oracle data-dictionary probes (ALL_TAB_PRIVS etc.) on Xugu.
		return false;
	}
}
