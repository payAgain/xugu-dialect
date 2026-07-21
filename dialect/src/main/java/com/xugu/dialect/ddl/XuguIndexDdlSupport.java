package com.xugu.dialect.ddl;

/**
 * XuGu advanced index DDL ({@code reference/object/indexes.md}).
 *
 * <p><b>A-SCH-017 known-limit:</b> Hibernate schema export emits basic B-tree indexes only
 * (A-SCH-016). Functional and BITMAP shapes documented here are for native SQL / tooling;
 * spatial / LOCAL / GLOBAL partition indexes are out of scope for this subset.
 */
public final class XuguIndexDdlSupport {

	/**
	 * Documented advanced index kinds delivered in P-008 (subset of {@code indexes.md}).
	 */
	public enum AdvancedIndexKind {
		/** Function expression index key ({@code indexes.md} §函数索引). */
		FUNCTIONAL,
		/** {@code INDEXTYPE IS BITMAP} ({@code indexes.md} §1.2.2). */
		BITMAP
	}

	private XuguIndexDdlSupport() {
	}

	/**
	 * {@code CREATE INDEX idx_func ON tab_test_2 (len(name))} ({@code indexes.md} §1.2.1.3).
	 */
	public static String createFunctionalIndexSql(String tableName, String indexName, String expression) {
		return "create index " + indexName + " on " + tableName + " (" + expression + ")";
	}

	/**
	 * {@code CREATE INDEX idx_bm_1 ON tab_test_2 (birth) INDEXTYPE IS BITMAP}
	 * ({@code indexes.md} §1.2.2.3).
	 */
	public static String createBitmapIndexSql(String tableName, String indexName, String columnName) {
		return "create index " + indexName + " on " + tableName + " (" + columnName + ") indextype is bitmap";
	}

	public static AdvancedIndexKind[] documentedAdvancedIndexKinds() {
		return AdvancedIndexKind.values();
	}
}
