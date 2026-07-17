package com.xugu.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

/**
 * XuGu IDENTITY column support (A-IDN-001..004).
 *
 * <p>DDL: prefer {@code identity(1,1)} in {@code compatiblemode=NONE}
 * ({@code AUTO_INCREMENT} ≡ {@code IDENTITY(1,1)} per create.md — not emitted).
 *
 * <p>Generated-key retrieval: {@link com.xugu.dialect.XuguDialect#getDefaultUseGetGeneratedKeys()}
 * is {@code false}, so Hibernate uses {@link #getIdentitySelectString}
 * ({@code select last_insert_id() from dual}) instead of JDBC {@code RETURN_GENERATED_KEYS}.
 * That avoids driver re-parse losing quotes on reserved table names (I-004 / P-002).
 */
public class XuguIdentityColumnSupport extends IdentityColumnSupportImpl {

	public static final XuguIdentityColumnSupport INSTANCE = new XuguIdentityColumnSupport();

	@Override
	public boolean supportsIdentityColumns() {
		return true;
	}

	/**
	 * Appended after the column type when {@link #hasDataTypeInIdentityColumn()} is true,
	 * e.g. {@code integer identity(1,1)}.
	 */
	@Override
	public String getIdentityColumnString(int type) {
		return "identity(1,1)";
	}

	/**
	 * Primary XuGu identity retrieval when {@code getDefaultUseGetGeneratedKeys} is false:
	 * {@code SELECT LAST_INSERT_ID() FROM dual}.
	 */
	@Override
	public String getIdentitySelectString(String table, String column, int type) {
		return "select last_insert_id() from dual";
	}
}
