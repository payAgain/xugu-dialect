package com.xugu.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;

/**
 * Hibernate 7.4 {@link DialectResolver} SPI for XuguDB (A-SPI-002/003/004).
 *
 * <p>Match rule (proven on live XuguDB JDBC metadata):
 * <ul>
 *   <li>{@code DatabaseMetaData.getDatabaseProductName()} → {@code XuguDB}</li>
 *   <li>{@code DatabaseMetaData.getDriverName()} → {@code XuguDB JDBC Driver}</li>
 * </ul>
 * Resolves when product or driver name contains {@code "xugu"} (case-insensitive).
 * Non-Xugu metadata (MySQL / Oracle / PostgreSQL) returns {@code null}.
 */
public class XuguDialectResolver implements DialectResolver {

	/**
	 * Case-insensitive token used against JDBC product / driver names.
	 * Live observation: product {@code XuguDB}, driver {@code XuguDB JDBC Driver}.
	 */
	public static final String XUGU_NAME_TOKEN = "xugu";

	@Override
	public Dialect resolveDialect(DialectResolutionInfo info) {
		if ( info == null || !matchesXugu( info ) ) {
			return null;
		}
		return new XuguDialect( info );
	}

	/**
	 * @return {@code true} when product name or driver name indicates XuguDB
	 */
	public static boolean matchesXugu(DialectResolutionInfo info) {
		return containsXuguToken( info.getDatabaseName() )
				|| containsXuguToken( info.getDriverName() );
	}

	public static boolean containsXuguToken(String value) {
		return value != null && value.toLowerCase().contains( XUGU_NAME_TOKEN );
	}
}
