package com.xugu.dialect.exception;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.ViolatedConstraintNameExtractor;

/**
 * Maps XuguDB JDBC errors to Hibernate exception subtypes (C-EXC-001).
 *
 * <p>Resolution order for vendor code: {@link SQLException#getErrorCode()}, then
 * {@code SQLState} prefix {@code xugu}, then message token {@code [ENNNNN]}.
 */
public final class XuguSQLExceptionConversionDelegate {

	private static final Pattern MESSAGE_ERROR_CODE = Pattern.compile( "\\[E(\\d{5})]" );

	private XuguSQLExceptionConversionDelegate() {
	}

	public static SQLExceptionConversionDelegate create(ViolatedConstraintNameExtractor constraintNames) {
		return (sqlException, message, sql) -> {
			final int code = resolveErrorCode( sqlException );
			return switch ( code ) {
				case XuguErrorCodes.UNIQUE_VIOLATION -> new ConstraintViolationException(
						message,
						sqlException,
						sql,
						ConstraintKind.UNIQUE,
						constraintNames.extractConstraintName( sqlException )
				);
				case XuguErrorCodes.FOREIGN_KEY_VIOLATION -> new ConstraintViolationException(
						message,
						sqlException,
						sql,
						ConstraintKind.FOREIGN_KEY,
						constraintNames.extractConstraintName( sqlException )
				);
				case XuguErrorCodes.CHECK_VIOLATION -> new ConstraintViolationException(
						message,
						sqlException,
						sql,
						ConstraintKind.CHECK,
						constraintNames.extractConstraintName( sqlException )
				);
				case XuguErrorCodes.NOT_NULL_VIOLATION -> new ConstraintViolationException(
						message,
						sqlException,
						sql,
						ConstraintKind.NOT_NULL,
						constraintNames.extractConstraintName( sqlException )
				);
				case XuguErrorCodes.DEADLOCK -> new LockAcquisitionException( message, sqlException, sql );
				case XuguErrorCodes.LOCK_TIMEOUT,
						XuguErrorCodes.LOCK_TIMEOUT_DETAIL,
						XuguErrorCodes.LOCK_UPGRADE_CONFLICT ->
						new LockTimeoutException( message, sqlException, sql );
				default -> null;
			};
		};
	}

	/**
	 * Visible for unit tests and the constraint-name extractor.
	 */
	public static int resolveErrorCode(SQLException sqlException) {
		final int errorCode = sqlException.getErrorCode();
		if ( errorCode != 0 ) {
			return errorCode;
		}
		final String sqlState = sqlException.getSQLState();
		if ( sqlState != null && sqlState.length() > 4
				&& sqlState.regionMatches( true, 0, "xugu", 0, 4 ) ) {
			try {
				return Integer.parseInt( sqlState.substring( 4 ) );
			}
			catch ( NumberFormatException ignored ) {
				// fall through
			}
		}
		final String errorMessage = sqlException.getMessage();
		if ( errorMessage != null ) {
			final Matcher matcher = MESSAGE_ERROR_CODE.matcher( errorMessage );
			if ( matcher.find() ) {
				try {
					return Integer.parseInt( matcher.group( 1 ) );
				}
				catch ( NumberFormatException ignored ) {
					// fall through
				}
			}
		}
		return 0;
	}
}
