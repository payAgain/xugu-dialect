package com.xugu.dialect.ddl;

/**
 * XuGu table DDL extensions ({@code reference/object/table/create.md},
 * {@code reference/object/table/partition.md}, {@code reference/object/encryptor.md}).
 *
 * <p><b>A-DDL-007:</b> {@code CREATE TABLE IF NOT EXISTS} is wired via C-DDL-001
 * ({@code XuguDialect#getCreateTableString()} / {@link #CREATE_TABLE_IF_NOT_EXISTS_PREFIX}) —
 * this class holds the SSOT cross-ref constant only; no duplicate dialect override.
 *
 * <p><b>A-DDL-008 known-limit:</b> Hibernate schema export does not emit {@code PARTITION BY}.
 * Documented LIST/RANGE/HASH shapes are for native SQL / schema tooling only.
 *
 * <p><b>A-DDL-009 known-limit:</b> {@code ENCRYPT BY} requires a pre-existing encryptor
 * ({@code CREATE ENCRYPTOR} needs SYSSSO / {@code ACL_SSO}). Schema export does not emit
 * {@code ENCRYPT BY}; native SQL IT skips when no encryptor is visible.
 */
public final class XuguTableDdlSupport {

	/** C-DDL-001 / A-DDL-007 SSOT cross-ref ({@code create.md#if_not_exists}). */
	public static final String CREATE_TABLE_IF_NOT_EXISTS_PREFIX = "create table if not exists";

	private XuguTableDdlSupport() {
	}

	/**
	 * Documented XuGu partition kinds ({@code partition.md} introduction).
	 */
	public enum PartitionKind {
		LIST,
		RANGE,
		HASH
	}

	/**
	 * {@code PARTITION BY LIST (key) PARTITIONS(…)} ({@code partition.md} §1.3 example 1).
	 */
	public static String createListPartitionTableSql(
			String tableName,
			String columnDefs,
			String partitionKey,
			String listPartitionsClause) {
		return "create table " + tableName + "(\n"
				+ columnDefs + "\n"
				+ ")\n"
				+ "partition by list (" + partitionKey + ")\n"
				+ "partitions(" + listPartitionsClause + ")";
	}

	/**
	 * {@code PARTITION BY RANGE (key) PARTITIONS (…)} ({@code partition.md} §1.3 example 2).
	 */
	public static String createRangePartitionTableSql(
			String tableName,
			String columnDefs,
			String partitionKey,
			String rangePartitionsClause) {
		return "create table " + tableName + "(\n"
				+ columnDefs + "\n"
				+ ")\n"
				+ "partition by range (" + partitionKey + ")\n"
				+ "partitions (\n"
				+ rangePartitionsClause + "\n"
				+ ")";
	}

	/**
	 * {@code PARTITION BY HASH (key) PARTITIONS n} ({@code partition.md} §1.3 example 3).
	 */
	public static String createHashPartitionTableSql(
			String tableName,
			String columnDefs,
			String partitionKey,
			int partitionCount) {
		return "create table " + tableName + "(\n"
				+ columnDefs + "\n"
				+ ")\n"
				+ "partition by hash (" + partitionKey + ")\n"
				+ "partitions " + partitionCount;
	}

	/**
	 * {@code ENCRYPT BY 'encryptor_name'} ({@code create.md#opt_encrypt}).
	 */
	public static String encryptByClause(String encryptorName) {
		return "encrypt by '" + encryptorName + "'";
	}

	/**
	 * {@code CREATE TABLE … (cols) ENCRYPT BY 'encryptor_name'} ({@code create.md}).
	 */
	public static String createTableWithEncryptBySql(
			String tableName,
			String columnDefs,
			String encryptorName) {
		return "create table " + tableName + "(\n"
				+ columnDefs + "\n"
				+ ") "
				+ encryptByClause( encryptorName );
	}

	/**
	 * {@code CREATE ENCRYPTOR 'name' BY 'key'} ({@code encryptor.md} §1.1).
	 */
	public static String createEncryptorSql(String encryptorName, String encryptionKey) {
		return "create encryptor '" + encryptorName + "' by '" + encryptionKey + "'";
	}

	public static PartitionKind[] documentedPartitionKinds() {
		return PartitionKind.values();
	}
}
