package com.xugu.demo.flyway;

import java.sql.Connection;
import java.util.Locale;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.database.base.Database;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.flywaydb.database.oracle.OracleDatabaseType;
import org.flywaydb.database.oracle.XuguDatabase;

/**
 * Flyway plugin: map Xugu JDBC to Oracle-compatible migration plumbing (B-FLY-001).
 * Migration SQL stays Xugu-native ({@code compatiblemode=NONE}); demo module only.
 */
public class XuguFlywayDatabaseType extends OracleDatabaseType {

	@Override
	public String getName() {
		return "XuguDB";
	}

	@Override
	public boolean handlesJDBCUrl(String url) {
		return url != null && url.toLowerCase( Locale.ROOT ).startsWith( "jdbc:xugu:" );
	}

	@Override
	public boolean handlesDatabaseProductNameAndVersion(String productName, String version, Connection connection) {
		if ( productName != null && productName.toLowerCase( Locale.ROOT ).contains( "xugu" ) ) {
			return true;
		}
		return super.handlesDatabaseProductNameAndVersion( productName, version, connection );
	}

	@Override
	public Database createDatabase(
			Configuration configuration,
			JdbcConnectionFactory connectionFactory,
			StatementInterceptor statementInterceptor) {
		return new XuguDatabase( configuration, connectionFactory, statementInterceptor );
	}
}
