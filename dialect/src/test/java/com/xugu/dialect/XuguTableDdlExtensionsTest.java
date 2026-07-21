package com.xugu.dialect;

import org.junit.jupiter.api.Test;

import com.xugu.dialect.ddl.XuguTableDdlSupport;
import com.xugu.dialect.ddl.XuguTableDdlSupport.PartitionKind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: table DDL extensions (A-DDL-007/008/009).
 */
class XuguTableDdlExtensionsTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void ifNotExistsPromotedViaC_DDL_001_A_DDL_007() {
		assertTrue( dialect.supportsIfExistsBeforeTableName(), "A-DDL-007 cross-ref C-DDL-001" );
		assertEquals( "create table if not exists", dialect.getCreateTableString() );
		assertEquals(
				XuguTableDdlSupport.CREATE_TABLE_IF_NOT_EXISTS_PREFIX,
				dialect.getCreateTableString() );
	}

	@Test
	void partitionSqlMatchesPartitionDoc_A_DDL_008() {
		assertEquals( 3, XuguTableDdlSupport.documentedPartitionKinds().length );
		assertEquals( PartitionKind.LIST, XuguTableDdlSupport.documentedPartitionKinds()[0] );
		assertEquals( PartitionKind.RANGE, XuguTableDdlSupport.documentedPartitionKinds()[1] );
		assertEquals( PartitionKind.HASH, XuguTableDdlSupport.documentedPartitionKinds()[2] );

		// partition.md §1.3 example 1 — tab_parti_1 LIST (city)
		assertEquals(
				"""
				create table tab_parti_1(
				id int,
				name varchar(20),
				city char(20)
				)
				partition by list (city)
				partitions(('四川'),('云南'),('贵州'),(othervalues))""",
				XuguTableDdlSupport.createListPartitionTableSql(
						"tab_parti_1",
						"id int,\nname varchar(20),\ncity char(20)",
						"city",
						"('四川'),('云南'),('贵州'),(othervalues)"
				)
		);

		// partition.md §1.3 example 2 — tab_parti_2 RANGE (id)
		assertEquals(
				"""
				create table tab_parti_2(
				id integer identity(1,2),
				name varchar,
				city varchar
				)
				partition by range (id)
				partitions (
				part1 values less than (1),
				part2 values less than (1000),
				part3 values less than (1500),
				part4 values less than (2000),
				part5 values less than (maxvalues)
				)""",
				XuguTableDdlSupport.createRangePartitionTableSql(
						"tab_parti_2",
						"id integer identity(1,2),\nname varchar,\ncity varchar",
						"id",
						"""
						part1 values less than (1),
						part2 values less than (1000),
						part3 values less than (1500),
						part4 values less than (2000),
						part5 values less than (maxvalues)"""
				)
		);

		// partition.md §1.3 example 3 — tab_parti_3 HASH (id) PARTITIONS 5
		assertEquals(
				"""
				create table tab_parti_3(
				id integer identity(1,2),
				name varchar,
				city varchar
				)
				partition by hash (id)
				partitions 5""",
				XuguTableDdlSupport.createHashPartitionTableSql(
						"tab_parti_3",
						"id integer identity(1,2),\nname varchar,\ncity varchar",
						"id",
						5
				)
		);
	}

	@Test
	void dialectDoesNotClaimPartitionInSchemaExport_A_DDL_008() {
		assertFalse(
				dialect.supportsPartitionByInSchemaExport(),
				"A-DDL-008 known-limit: Hibernate schema export does not emit PARTITION BY" );
	}

	@Test
	void encryptSqlMatchesCreateDoc_A_DDL_009() {
		assertEquals(
				"encrypt by 'enc_1'",
				XuguTableDdlSupport.encryptByClause( "enc_1" )
		);
		assertEquals(
				"""
				create table tab_encrypt(
				id int primary key,
				payload varchar(32)
				) encrypt by 'enc_1'""",
				XuguTableDdlSupport.createTableWithEncryptBySql(
						"tab_encrypt",
						"id int primary key,\npayload varchar(32)",
						"enc_1"
				)
		);
		assertEquals(
				"create encryptor 'enc_1' by 'enc_key_value'",
				XuguTableDdlSupport.createEncryptorSql( "enc_1", "enc_key_value" )
		);
	}

	@Test
	void dialectDoesNotClaimEncryptInSchemaExport_A_DDL_009() {
		assertFalse(
				dialect.supportsEncryptByInSchemaExport(),
				"A-DDL-009 known-limit: ENCRYPT BY requires SYSSSO encryptor + no schema export hook" );
	}
}
