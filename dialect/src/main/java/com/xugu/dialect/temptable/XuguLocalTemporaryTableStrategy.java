package com.xugu.dialect.temptable;

import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.dialect.temptable.TemporaryTableStrategy;
import org.hibernate.query.sqm.mutation.spi.AfterUseAction;
import org.hibernate.query.sqm.mutation.spi.BeforeUseAction;

/**
 * Local temporary tables for XuguDB (A-SCH-004 / A-SCH-006).
 *
 * <p>Docs ({@code reference/object/table/create.md} OptTemp): {@code TEMPORARY} /
 * {@code TEMP} / {@code LOCAL TEMPORARY} / {@code LOCAL TEMP} all create a
 * <em>local</em> temporary table (session-scoped structure + data).
 *
 * <p>{@code ON COMMIT DELETE|PRESERVE ROWS} is documented for temp tables only;
 * omitting the clause preserves rows on commit. This strategy emits
 * {@code on commit preserve rows} explicitly so Hibernate mutation strategies
 * see a stable option string.
 *
 * <p><b>A-SCH-007:</b> Xugu forbids FK on temporary tables. Hibernate's
 * {@link org.hibernate.dialect.temptable.StandardTemporaryTableExporter} never
 * emits foreign-key clauses — do not add them here.
 */
public final class XuguLocalTemporaryTableStrategy implements TemporaryTableStrategy {

	public static final XuguLocalTemporaryTableStrategy INSTANCE = new XuguLocalTemporaryTableStrategy();

	/** Locked CREATE command fragment (A-SCH-004). */
	public static final String CREATE_COMMAND = "create local temporary table";

	/** Locked ON COMMIT option (A-SCH-006). */
	public static final String CREATE_OPTIONS = "on commit preserve rows";

	private XuguLocalTemporaryTableStrategy() {
	}

	@Override
	public String adjustTemporaryTableName(String desiredTableName) {
		return desiredTableName;
	}

	@Override
	public TemporaryTableKind getTemporaryTableKind() {
		return TemporaryTableKind.LOCAL;
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
		return AfterUseAction.NONE;
	}

	@Override
	public BeforeUseAction getTemporaryTableBeforeUseAction() {
		return BeforeUseAction.CREATE;
	}
}
