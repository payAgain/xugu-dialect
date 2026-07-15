package com.xugu.dialect.pagination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.dialect.pagination.AbstractSimpleLimitHandler;
import org.hibernate.sql.ast.spi.ParameterMarkerStrategy;

/**
 * XuGu pagination via {@code LIMIT} / {@code LIMIT count OFFSET offset}.
 *
 * <p><b>Stable form choice (A-PAG-002):</b> {@code LIMIT count OFFSET offset}
 * (not {@code LIMIT offset, count}). Both are documented in
 * {@code reference/sql/select/resultset-restricted.md}; this form matches
 * Hibernate {@code LimitOffsetLimitHandler} bind order
 * ({@code bindLimitParametersInReverseOrder=true} → bind count then offset).
 *
 * <p><b>Clause order with locks:</b> XuGu {@code select_no_parens} is
 * {@code sort_clause? opt_for_update_clause? opt_select_limit?}
 * ({@code reference/sql/select/select.md}). Live Xugu under
 * {@code compatiblemode=NONE} accepts:
 * <ul>
 *   <li>{@code … FOR UPDATE LIMIT n} — yes</li>
 *   <li>{@code … LIMIT n FOR UPDATE} — no (Hibernate default; rejected)</li>
 *   <li>{@code … FOR UPDATE LIMIT n WAIT ms} — yes</li>
 *   <li>{@code … FOR UPDATE WAIT ms LIMIT n} — no</li>
 * </ul>
 * This handler therefore appends LIMIT at end, or inserts it
 * <em>before</em> a trailing {@code NOWAIT}/{@code WAIT} clause.
 *
 * <p>Does <b>not</b> emit {@code FETCH FIRST} (A-PAG-005).
 */
public class XuguLimitHandler extends AbstractSimpleLimitHandler {

	public static final XuguLimitHandler INSTANCE = new XuguLimitHandler();

	/** Trailing XuGu wait clause after FOR UPDATE (inline form). */
	private static final Pattern TRAILING_WAIT =
			Pattern.compile( "(?i)\\s+(nowait|wait(?:\\s+\\d+)?)\\s*$" );

	@Override
	protected String limitClause(boolean hasFirstRow) {
		return hasFirstRow ? " limit ? offset ?" : " limit ?";
	}

	@Override
	protected String limitClause(boolean hasFirstRow, int jdbcParameterCount, ParameterMarkerStrategy markerStrategy) {
		String limit = " limit " + markerStrategy.createMarker( jdbcParameterCount + 1, null );
		if ( !hasFirstRow ) {
			return limit;
		}
		return limit + " offset " + markerStrategy.createMarker( jdbcParameterCount + 2, null );
	}

	@Override
	protected String offsetOnlyClause() {
		// XuGu LIMIT max is 2147483647 (resultset-restricted.md)
		return " limit 2147483647 offset ?";
	}

	@Override
	protected String offsetOnlyClause(int jdbcParameterCount, ParameterMarkerStrategy markerStrategy) {
		return " limit 2147483647 offset " + markerStrategy.createMarker( jdbcParameterCount + 1, null );
	}

	/**
	 * Place LIMIT after {@code FOR UPDATE} but before trailing {@code NOWAIT}/{@code WAIT}.
	 */
	@Override
	protected String insert(String limitClause, String sql) {
		Matcher wait = TRAILING_WAIT.matcher( sql );
		if ( wait.find() ) {
			return sql.substring( 0, wait.start() ) + limitClause + wait.group();
		}
		return insertAtEnd( limitClause, sql );
	}

	@Override
	public final boolean bindLimitParametersInReverseOrder() {
		// Clause is LIMIT count OFFSET offset → bind maxRows then firstRow
		return true;
	}

	@Override
	public boolean supportsOffset() {
		return true;
	}

	@Override
	public boolean processSqlMutatesState() {
		return false;
	}
}
