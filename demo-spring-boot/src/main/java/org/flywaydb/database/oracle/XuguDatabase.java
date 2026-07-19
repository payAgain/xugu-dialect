package org.flywaydb.database.oracle;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;

/**
 * Flyway database handle for Xugu (demo consumer path). Package matches {@link OracleConnection}
 * visibility for schema probe override.
 */
public class XuguDatabase extends OracleDatabase {

	public XuguDatabase(
			Configuration configuration,
			JdbcConnectionFactory connectionFactory,
			StatementInterceptor statementInterceptor) {
		super( configuration, connectionFactory, statementInterceptor );
	}

	@Override
	protected OracleConnection doGetConnection(java.sql.Connection connection) {
		return new XuguConnection( this, connection );
	}

	@Override
	protected String doGetCatalog() {
		// Xugu has no GLOBAL_NAME view; demo JDBC URL targets SYSTEM catalog.
		return "SYSTEM";
	}

	@Override
	protected String doGetCurrentUser() {
		return "SYSDBA";
	}
}
