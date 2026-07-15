package com.xugu.dialect.identity;

import org.hibernate.dialect.identity.IdentityColumnSupportImpl;

/**
 * XuGu IDENTITY column support (A-IDN-001..004).
 *
 * <p>DDL: prefer {@code identity(1,1)} in {@code compatiblemode=NONE}
 * ({@code AUTO_INCREMENT} ≡ {@code IDENTITY(1,1)} per create.md — not emitted).
 *
 * <p>Generated-key retrieval: Hibernate uses JDBC {@code getGeneratedKeys} by default
 * ({@code Dialect#getDefaultUseGetGeneratedKeys()} == true; live JDBC driver proven).
 * {@link #getIdentitySelectString} provides documented {@code LAST_INSERT_ID()} fallback
 * when a select-based path is required.
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
	 * Documented XuGu fallback when not using JDBC getGeneratedKeys:
	 * {@code SELECT LAST_INSERT_ID() FROM dual}.
	 */
	@Override
	public String getIdentitySelectString(String table, String column, int type) {
		return "select last_insert_id() from dual";
	}
}
