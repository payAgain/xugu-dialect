package com.xugu.dialect.type;

/**
 * XuGu user-defined type (UDT) DDL helpers ({@code reference/sql/datatype/udt.md}).
 *
 * <p><b>A-TYP-018 known-limit:</b> XuGu documents three UDT families — {@code OBJECT}
 * (structure), {@code VARRAY} (bounded array), and {@code TABLE} (nested table). Hibernate 7.4
 * exposes no {@code SqlTypes} for schema-defined UDT columns; JDBC {@code STRUCT}/custom
 * descriptors are unverified on Xugu. This class locks documented {@code CREATE TYPE} /
 * {@code DROP TYPE} shapes and column-type naming (existing UDT type name) for native SQL /
 * schema tooling only — not ORM entity attribute mapping.
 */
public final class XuguUdtTypeSupport {

	/** Documented maximum VARRAY element count ({@code udt.md} §数组类型). */
	public static final int VARRAY_MAX_CAPACITY = 65_535;

	private XuguUdtTypeSupport() {
	}

	/**
	 * Documented XuGu UDT families ({@code udt.md} introduction table).
	 */
	public enum Kind {
		OBJECT,
		VARRAY,
		TABLE
	}

	/**
	 * {@code CREATE OR REPLACE TYPE type_name AS OBJECT (member_decls)}
	 * ({@code udt.md} §结构类型 — example {@code udt_obj_type}).
	 */
	public static String createObjectTypeSql(String typeName, String memberDecls) {
		return "create or replace type " + typeName + " as object (" + memberDecls + ")";
	}

	/**
	 * {@code CREATE OR REPLACE TYPE type_name IS VARRAY(capacity) OF elementType}
	 * ({@code udt.md} §数组类型 — example {@code var_test IS VARRAY(3) OF VARCHAR}).
	 */
	public static String createVarrayTypeSql(String typeName, int capacity, String elementTypeName) {
		return "create or replace type " + typeName + " is varray(" + capacity + ") of " + elementTypeName;
	}

	/**
	 * {@code CREATE OR REPLACE TYPE type_name IS TABLE OF elementType}
	 * ({@code udt.md} §嵌套表类型 — example {@code udt_tab_type IS TABLE OF BIGINT}).
	 */
	public static String createTableTypeSql(String typeName, String elementTypeName) {
		return "create or replace type " + typeName + " is table of " + elementTypeName;
	}

	/**
	 * {@code DROP TYPE type_name} ({@code udt.md} §删除类型).
	 */
	public static String dropTypeSql(String typeName) {
		return "drop type " + typeName;
	}

	/**
	 * Column type for an existing schema UDT is the type name itself
	 * ({@code udt.md} — e.g. {@code udt_obj UDT_OBJ_TYPE}).
	 */
	public static String columnTypeFor(String udtTypeName) {
		return udtTypeName;
	}

	public static Kind[] documentedKinds() {
		return Kind.values();
	}
}
