package com.xugu.dialect;

import java.sql.SQLException;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.exception.XuguErrorCodes;
import com.xugu.dialect.exception.XuguSQLExceptionConversionDelegate;
import com.xugu.dialect.exception.XuguViolatedConstraintNameExtractor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Offline unit coverage for C-EXC-001/002 wiring and error-code resolution.
 */
class XuguExceptionConversionTest {

	@Test
	void dialectWiresConversionDelegateAndExtractor() {
		XuguDialect dialect = new XuguDialect();
		assertNotNull( dialect.buildSQLExceptionConversionDelegate() );
		assertSame( XuguViolatedConstraintNameExtractor.INSTANCE, dialect.getViolatedConstraintNameExtractor() );
	}

	@Test
	void resolveErrorCodePrefersJdbcErrorCode() {
		SQLException ex = new SQLException( "x", "xugu00000", XuguErrorCodes.UNIQUE_VIOLATION );
		assertEquals( XuguErrorCodes.UNIQUE_VIOLATION, XuguSQLExceptionConversionDelegate.resolveErrorCode( ex ) );
	}

	@Test
	void resolveErrorCodeFallsBackToSqlStateSuffix() {
		SQLException ex = new SQLException( "msg", "xugu13005", 0 );
		assertEquals( XuguErrorCodes.FOREIGN_KEY_VIOLATION, XuguSQLExceptionConversionDelegate.resolveErrorCode( ex ) );
	}

	@Test
	void resolveErrorCodeFallsBackToMessageToken() {
		SQLException ex = new SQLException( "[E13008] 违反值检查约束", null, 0 );
		assertEquals( XuguErrorCodes.CHECK_VIOLATION, XuguSQLExceptionConversionDelegate.resolveErrorCode( ex ) );
	}

	@Test
	void conversionMapsUniqueViolation() {
		SQLExceptionConversionDelegate delegate = XuguSQLExceptionConversionDelegate.create(
				XuguViolatedConstraintNameExtractor.INSTANCE );
		SQLException sqlEx = new SQLException( "[E13001] 违反唯一值约束", "xugu13001", XuguErrorCodes.UNIQUE_VIOLATION );
		var converted = delegate.convert( sqlEx, "persist", "insert into t" );
		ConstraintViolationException cve = assertInstanceOf( ConstraintViolationException.class, converted );
		assertEquals( ConstraintKind.UNIQUE, cve.getKind() );
	}

	@Test
	void conversionMapsDeadlockAndLockTimeout() {
		SQLExceptionConversionDelegate delegate = XuguSQLExceptionConversionDelegate.create(
				XuguViolatedConstraintNameExtractor.INSTANCE );
		assertInstanceOf(
				LockAcquisitionException.class,
				delegate.convert(
						new SQLException( "deadlock", "xugu14001", XuguErrorCodes.DEADLOCK ),
						"m",
						"sql" )
		);
		assertInstanceOf(
				LockTimeoutException.class,
				delegate.convert(
						new SQLException( "busy", "xugu14011", XuguErrorCodes.LOCK_TIMEOUT ),
						"m",
						"sql" )
		);
	}

	@Test
	void extractorParsesNotNullFieldName() {
		SQLException ex = new SQLException( "[E16005] 字段 email 不能取空值", "xugu16005", XuguErrorCodes.NOT_NULL_VIOLATION );
		assertEquals( "email", XuguViolatedConstraintNameExtractor.INSTANCE.extractConstraintName( ex ) );
	}

	@Test
	void extractorReturnsNullWhenNameAbsent() {
		SQLException ex = new SQLException( "[E13001] 违反唯一值约束", "xugu13001", XuguErrorCodes.UNIQUE_VIOLATION );
		assertNull( XuguViolatedConstraintNameExtractor.INSTANCE.extractConstraintName( ex ) );
	}
}
