package com.xugu.dialect.temptable;

import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.dialect.temptable.TemporaryTableStrategy;
import org.hibernate.query.sqm.mutation.spi.AfterUseAction;
import org.hibernate.query.sqm.mutation.spi.BeforeUseAction;

/**
 * Global temporary tables for XuguDB (A-SCH-005 / A-SCH-006).
 *
 * <p><b>Precondition:</b> server parameter {@code support_global_tab} must be
 * {@code ON}/{@code true}. Default is {@code FALSE}
 * ({@code reference/system-configuration-parameter/xugu.ini/sql-engine/support_global_tab.md}).
 * Creating a global temp table while the parameter is off raises E5073.
 *
 * <p>Docs ({@code reference/object/table/create.md} OptTemp):
 * {@code GLOBAL TEMPORARY} / {@code GLOBAL TEMP} — structure persists, data is
 * session-scoped. {@code ON COMMIT DELETE ROWS} matches Hibernate's global
 * temp after-use CLEAN semantics.
 *
 * <p><b>A-SCH-007:</b> do not emit FK on temporary tables (exporter never does).
 */
public final class XuguGlobalTemporaryTableStrategy implements TemporaryTableStrategy {

	public static final XuguGlobalTemporaryTableStrategy INSTANCE = new XuguGlobalTemporaryTableStrategy();

	/** Locked CREATE command fragment (A-SCH-005). */
	public static final String CREATE_COMMAND = "create global temporary table";

	/** Locked ON COMMIT option (A-SCH-006); aligns with AfterUseAction.CLEAN. */
	public static final String CREATE_OPTIONS = "on commit delete rows";

	/**
	 * Documented server parameter that must be enabled before CREATE GLOBAL TEMP.
	 */
	public static final String SUPPORT_GLOBAL_TAB_PARAM = "support_global_tab";

	private XuguGlobalTemporaryTableStrategy() {
	}

	@Override
	public String adjustTemporaryTableName(String desiredTableName) {
		return desiredTableName;
	}

	@Override
	public TemporaryTableKind getTemporaryTableKind() {
		return TemporaryTableKind.GLOBAL;
	}

	@Override
	public String getTemporaryTableCreateOptions() {
		return CREATE_OPTIONS;
	}

	@Override
	public String getTemporaryTableCreateCommand() {
		return CREATE_COMMAND;
	}

	@Override
	public String getTemporaryTableDropCommand() {
		return "drop table";
	}

	@Override
	public String getTemporaryTableTruncateCommand() {
		return "truncate table";
	}

	@Override
	public String getCreateTemporaryTableColumnAnnotation(int sqlTypeCode) {
		return "";
	}

	@Override
	public AfterUseAction getTemporaryTableAfterUseAction() {
		return AfterUseAction.CLEAN;
	}

	@Override
	public BeforeUseAction getTemporaryTableBeforeUseAction() {
		return BeforeUseAction.NONE;
	}
}
