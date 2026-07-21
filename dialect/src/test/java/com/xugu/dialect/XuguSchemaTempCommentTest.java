package com.xugu.dialect;

import org.hibernate.dialect.temptable.StandardTemporaryTableExporter;
import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.temptable.XuguGlobalTemporaryTableStrategy;
import com.xugu.dialect.temptable.XuguLocalTemporaryTableStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for P-007 schema / temp / comment / FK / truncate / index SQL fragments.
 */
class XuguSchemaTempCommentTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void schemaCreateDropCommands_A_SCH_001() {
		assertTrue( dialect.canCreateSchema() );
		assertEquals( "create schema HIB_P007_SCH", dialect.getCreateSchemaCommand( "HIB_P007_SCH" )[0] );
		assertEquals( "drop schema HIB_P007_SCH", dialect.getDropSchemaCommand( "HIB_P007_SCH" )[0] );
		assertEquals( "select current_schema()", dialect.getCurrentSchemaCommand() );
	}

	@Test
	void nameQualifierIsSchemaOnly_A_SCH_002() {
		assertEquals( NameQualifierSupport.SCHEMA, dialect.getNameQualifierSupport() );
		assertTrue( dialect.getNameQualifierSupport().supportsSchemas() );
	}

	@Test
	void localTempStrategy_A_SCH_004_006() {
		assertEquals( TemporaryTableKind.LOCAL, dialect.getSupportedTemporaryTableKind() );
		assertSame( XuguLocalTemporaryTableStrategy.INSTANCE, dialect.getLocalTemporaryTableStrategy() );
		assertEquals( "create local temporary table", dialect.getTemporaryTableCreateCommand() );
		assertEquals( "on commit preserve rows", dialect.getTemporaryTableCreateOptions() );
		assertEquals(
				XuguLocalTemporaryTableStrategy.CREATE_COMMAND,
				dialect.getLocalTemporaryTableStrategy().getTemporaryTableCreateCommand() );
		assertEquals(
				XuguLocalTemporaryTableStrategy.CREATE_OPTIONS,
				dialect.getLocalTemporaryTableStrategy().getTemporaryTableCreateOptions() );
	}

	@Test
	void globalTempStrategy_A_SCH_005_006_preconditionDocumented() {
		assertSame( XuguGlobalTemporaryTableStrategy.INSTANCE, dialect.getGlobalTemporaryTableStrategy() );
		assertEquals(
				"create global temporary table",
				dialect.getGlobalTemporaryTableStrategy().getTemporaryTableCreateCommand() );
		assertEquals(
				"on commit delete rows",
				dialect.getGlobalTemporaryTableStrategy().getTemporaryTableCreateOptions() );
		assertEquals( "support_global_tab", XuguGlobalTemporaryTableStrategy.SUPPORT_GLOBAL_TAB_PARAM );
	}

	@Test
	void tempTableExporterDoesNotEmitFk_A_SCH_007() {
		// StandardTemporaryTableExporter builds column DDL only — never FK (A-SCH-007).
		assertInstanceOf( StandardTemporaryTableExporter.class, dialect.getTemporaryTableExporter() );
		String create = dialect.getTemporaryTableCreateCommand() + " HIB_P007_TMP (id integer) "
				+ dialect.getTemporaryTableCreateOptions();
		assertFalse( create.toLowerCase().contains( "foreign key" ) );
		assertFalse( create.toLowerCase().contains( "references" ) );
	}

	@Test
	void commentOnAndInline_A_SCH_008_009_010() {
		assertTrue( dialect.supportsCommentOn() );
		assertEquals( "", dialect.getTableComment( "ignored" ) );
		assertEquals( "", dialect.getColumnComment( "ignored" ) );
		assertEquals(
				"comment on table HIB_P007_T is 'tbl'",
				XuguDialect.commentOnTableSql( "HIB_P007_T", "tbl" ) );
		assertEquals(
				"comment on column HIB_P007_T.C1 is 'col'",
				XuguDialect.commentOnColumnSql( "HIB_P007_T.C1", "col" ) );
		assertEquals( " comment 'inline-t'", XuguDialect.inlineTableComment( "inline-t" ) );
		assertEquals( " comment 'inline-c'", XuguDialect.inlineColumnComment( "inline-c" ) );
	}

	@Test
	void uniqueFkCheckAlterTruncateIndex_A_SCH_011_to_016() {
		assertInstanceOf( CreateTableUniqueDelegate.class, dialect.getUniqueDelegate() );

		String fk = dialect.getAddForeignKeyConstraintString(
				"fk_child_parent",
				new String[] { "parent_id" },
				"HIB_P007_PARENT",
				new String[] { "id" },
				false );
		assertTrue( fk.contains( "add constraint" ) );
		assertTrue( fk.contains( "foreign key (parent_id)" ) );
		assertTrue( fk.contains( "references HIB_P007_PARENT" ) );
		assertTrue( fk.contains( "(id)" ) );

		assertEquals( "drop constraint", dialect.getDropForeignKeyString() );
		assertEquals( "drop constraint", dialect.getDropUniqueKeyString() );
		assertTrue( dialect.supportsColumnCheck() );
		assertTrue( dialect.supportsTableCheck() );
		assertTrue( dialect.supportsCascadeDelete() );

		assertEquals( "truncate table HIB_P007_T", dialect.getTruncateTableStatement( "HIB_P007_T" ) );
		assertEquals( "create index", dialect.getCreateIndexString( false ) );
		assertEquals( "create unique index", dialect.getCreateIndexString( true ) );
		assertTrue( dialect.qualifyIndexName() );
	}
}
