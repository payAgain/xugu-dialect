package com.xugu.dialect.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.hibernate.service.spi.Configurable;
import org.hibernate.service.spi.Stoppable;

/**
 * Offline ConnectionProvider: configures without connecting (unit tests for function registry).
 */
public final class OfflineConnectionProvider implements ConnectionProvider, Configurable, Stoppable {

	@Override
	public void configure(Map<String, Object> configurationValues) {
		// intentionally no JDBC
	}

	@Override
	public Connection getConnection() throws SQLException {
		throw new SQLException( "OfflineConnectionProvider: no JDBC in unit test" );
	}

	@Override
	public void closeConnection(Connection conn) {
		// no-op
	}

	@Override
	public boolean supportsAggressiveRelease() {
		return false;
	}

	@Override
	public boolean isUnwrappableAs(Class<?> unwrapType) {
		return ConnectionProvider.class.equals( unwrapType )
				|| OfflineConnectionProvider.class.isAssignableFrom( unwrapType );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public <T> T unwrap(Class<T> unwrapType) {
		if ( isUnwrappableAs( unwrapType ) ) {
			return (T) this;
		}
		throw new UnknownUnwrapTypeException( unwrapType );
	}

	@Override
	public void stop() {
		// no-op
	}
}
