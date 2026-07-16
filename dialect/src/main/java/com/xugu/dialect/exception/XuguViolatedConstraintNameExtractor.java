package com.xugu.dialect.exception;

import java.sql.SQLException;

import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtractor;
import org.hibernate.exception.spi.ViolatedConstraintNameExtractor;

import static org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtractor.extractUsingTemplate;

/**
 * Extracts violated field/constraint tokens from XuguDB error messages (C-EXC-002).
 *
 * <p>E16005 messages look like {@code [E16005] 字段 email 不能取空值}
 * ({@code reference/error-code/e16.md}, constraints docs).
 * When the driver message lacks a parseable name, returns {@code null}.
 */
public final class XuguViolatedConstraintNameExtractor {

	public static final ViolatedConstraintNameExtractor INSTANCE =
			new TemplatedViolatedConstraintNameExtractor( XuguViolatedConstraintNameExtractor::extract );

	private XuguViolatedConstraintNameExtractor() {
	}

	static String extract(SQLException sqlException) {
		final String message = sqlException.getMessage();
		if ( message == null ) {
			return null;
		}
		final int code = XuguSQLExceptionConversionDelegate.resolveErrorCode( sqlException );
		if ( code == XuguErrorCodes.NOT_NULL_VIOLATION ) {
			return trimToken( extractUsingTemplate( "字段", "不能取空值", message ) );
		}
		return null;
	}

	private static String trimToken(String token) {
		if ( token == null ) {
			return null;
		}
		final String trimmed = token.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
