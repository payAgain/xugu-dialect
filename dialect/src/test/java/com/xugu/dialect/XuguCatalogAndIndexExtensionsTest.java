package com.xugu.dialect;

import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.ddl.XuguIndexDdlSupport;
import com.xugu.dialect.ddl.XuguIndexDdlSupport.AdvancedIndexKind;
import com.xugu.dialect.metadata.XuguCatalogMetadataSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: A-SCH-003 catalog JDBC alignment + A-SCH-017 advanced index DDL.
 */
class XuguCatalogAndIndexExtensionsTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void catalogMetadataQueryLocked_A_SCH_003() {
		assertEquals( "select current_db()", XuguCatalogMetadataSupport.CURRENT_CATALOG_QUERY );
	}

	@Test
	void nameQualifierRemainsSchemaOnly_A_SCH_003() {
		assertEquals( NameQualifierSupport.SCHEMA, dialect.getNameQualifierSupport() );
		assertTrue( dialect.getNameQualifierSupport().supportsSchemas() );
		assertFalse( dialect.getNameQualifierSupport().supportsCatalogs(),
				"catalog.schema.table not emitted — DATABASE is connection-only" );
	}

	@Test
	void dialectDoesNotClaimCatalogInObjectNames_A_SCH_003() {
		assertFalse( dialect.supportsCatalogQualifierInObjectNames() );
		assertTrue( dialect.canCreateCatalog(), "C-CAT-001 create/drop database remains available" );
		assertEquals( "create database db_test", dialect.getCreateCatalogCommand( "db_test" )[0] );
	}

	@Test
	void functionalIndexSqlMatchesIndexesDoc_A_SCH_017() {
		assertEquals( 2, XuguIndexDdlSupport.documentedAdvancedIndexKinds().length );
		assertEquals( AdvancedIndexKind.FUNCTIONAL, XuguIndexDdlSupport.documentedAdvancedIndexKinds()[0] );
		assertEquals( AdvancedIndexKind.BITMAP, XuguIndexDdlSupport.documentedAdvancedIndexKinds()[1] );

		// indexes.md §1.2.1.3 — idx_func ON tab_test_2 (len(name))
		assertEquals(
				"create index idx_func on tab_test_2 (len(name))",
				XuguIndexDdlSupport.createFunctionalIndexSql( "tab_test_2", "idx_func", "len(name)" )
		);
	}

	@Test
	void bitmapIndexSqlMatchesIndexesDoc_A_SCH_017() {
		// indexes.md §1.2.2.3 — idx_bm_1 ON tab_test_2 (birth) INDEXTYPE IS BITMAP
		assertEquals(
				"create index idx_bm_1 on tab_test_2 (birth) indextype is bitmap",
				XuguIndexDdlSupport.createBitmapIndexSql( "tab_test_2", "idx_bm_1", "birth" )
		);
	}

	@Test
	void dialectDoesNotClaimAdvancedIndexInSchemaExport_A_SCH_017() {
		assertFalse( dialect.supportsAdvancedIndexInSchemaExport() );
		assertEquals( "create index", dialect.getCreateIndexString( false ) );
		assertEquals( "create unique index", dialect.getCreateIndexString( true ) );
	}
}
