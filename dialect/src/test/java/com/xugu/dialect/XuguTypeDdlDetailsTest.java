package com.xugu.dialect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

import org.hibernate.sql.ast.spi.StringBuilderSqlAppender;
import org.junit.jupiter.api.Test;

import jakarta.persistence.TemporalType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for I-003 / P-006 Type/DDL details (C-DDL / C-CAT / C-GUID).
 * Subquery-on-mutating-table is out of scope here (covered by P-005) — N/A.
 */
class XuguTypeDdlDetailsTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void createTableIfNotExists_C_DDL_001() {
		assertEquals( "create table if not exists", dialect.getCreateTableString() );
		assertTrue( dialect.supportsIfExistsBeforeTableName() );
		assertFalse( dialect.supportsIfExistsAfterTableName() );
	}

	@Test
	void alterColumnType_C_DDL_002() {
		assertTrue( dialect.supportsAlterColumnType() );
		assertEquals(
				"alter column payload varchar(64)",
				dialect.getAlterColumnTypeString( "payload", "varchar(64)", "varchar(64)" ) );
		assertEquals(
				"alter column payload varchar(64) not null",
				dialect.getAlterColumnTypeString( "payload", "varchar(64)", " varchar(64) not null " ) );
	}

	@Test
	void datetimeLiteralAndFormat_C_DDL_003() {
		StringBuilderSqlAppender dateBuf = new StringBuilderSqlAppender();
		dialect.appendDateTimeLiteral(
				dateBuf,
				LocalDate.of( 2025, 6, 20 ),
				TemporalType.DATE,
				TimeZone.getTimeZone( "UTC" ) );
		assertEquals( "date '2025-06-20'", dateBuf.toString() );

		StringBuilderSqlAppender tsBuf = new StringBuilderSqlAppender();
		dialect.appendDateTimeLiteral(
				tsBuf,
				LocalDateTime.of( 2025, 6, 20, 15, 16, 25 ),
				TemporalType.TIMESTAMP,
				TimeZone.getTimeZone( "UTC" ) );
		assertTrue( tsBuf.toString().startsWith( "timestamp '" ), tsBuf.toString() );
		assertTrue( tsBuf.toString().contains( "2025-06-20" ), tsBuf.toString() );

		StringBuilderSqlAppender fmtBuf = new StringBuilderSqlAppender();
		dialect.appendDatetimeFormat( fmtBuf, "yyyy-MM-dd" );
		assertEquals( "%Y-%m-%d", fmtBuf.toString() );
	}

	@Test
	void enumTypeDeclarationIsNull_C_DDL_004() {
		assertNull( dialect.getEnumTypeDeclaration( "Status", new String[] { "A", "B" } ) );
	}

	@Test
	void catalogCreateDrop_C_CAT_001() {
		assertTrue( dialect.canCreateCatalog() );
		assertEquals( "create database hib_i003_p006_cat", dialect.getCreateCatalogCommand( "hib_i003_p006_cat" )[0] );
		assertEquals( "drop database hib_i003_p006_cat", dialect.getDropCatalogCommand( "hib_i003_p006_cat" )[0] );
	}

	@Test
	void selectGuidString_C_GUID_001() {
		assertEquals( "select sys_guid()", dialect.getSelectGUIDString() );
	}
}
