package com.xugu.dialect.internal;

/**
 * Reserved / TCL keywords registered for Hibernate identifier quoting.
 * Sourced from {@code reference/sql/keyword.md} and {@code reference/sql/tcl.md}
 * (subset sufficient for P-003 quoting + TCL smoke).
 */
public final class XuguKeywords {

	private XuguKeywords() {
	}

	public static final String[] RESERVED = {
			"add", "all", "alter", "and", "any", "as", "asc", "between", "by",
			"case", "cast", "check", "column", "constraint", "create", "cross",
			"current_date", "current_time", "current_timestamp", "default", "delete",
			"desc", "distinct", "drop", "else", "end", "except", "exists", "false",
			"for", "foreign", "from", "full", "group", "having", "in", "index",
			"inner", "insert", "intersect", "into", "is", "join", "key", "left",
			"like", "limit", "not", "null", "on", "or", "order", "outer",
			"primary", "references", "right", "select", "set", "some", "table",
			"then", "to", "true", "union", "unique", "update", "using", "values",
			"when", "where", "with",
			// TCL (A-XCUT-004)
			"begin", "commit", "rollback", "transaction",
			// common DDL verbs / types that collide as identifiers
			"boolean", "date", "time", "timestamp", "datetime", "user", "sysdate"
	};
}
