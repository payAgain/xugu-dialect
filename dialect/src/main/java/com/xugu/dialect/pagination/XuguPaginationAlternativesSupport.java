package com.xugu.dialect.pagination;

/**
 * XuGu alternate pagination syntax ({@code reference/sql/select/resultset-restricted.md} TOP,
 * {@code reference/sql/select/select.md} §ROWNUM).
 *
 * <p><b>A-PAG-004 / A-PAG-006 known-limit:</b> Hibernate {@link XuguLimitHandler} and
 * {@code SqlAstTranslator} stay on {@code LIMIT} / {@code LIMIT … OFFSET …}. TOP and ROWNUM
 * are native SQL alternates only — doc: TOP is mutually exclusive with LIMIT.
 */
public final class XuguPaginationAlternativesSupport {

	private XuguPaginationAlternativesSupport() {
	}

	/**
	 * {@code SELECT TOP n projection FROM … [ORDER BY …]} ({@code resultset-restricted.md} §TOP;
	 * {@code select.md} {@code opt_top}).
	 */
	public static String selectTopSql(int topN, String projection, String fromClause, String orderByClause) {
		if ( topN <= 0 ) {
			throw new IllegalArgumentException( "topN must be positive" );
		}
		final StringBuilder sb = new StringBuilder( "select top " );
		sb.append( topN ).append( ' ' ).append( projection ).append( " from " ).append( fromClause );
		if ( orderByClause != null && !orderByClause.isBlank() ) {
			sb.append( " order by " ).append( orderByClause );
		}
		return sb.toString();
	}

	/**
	 * {@code SELECT rownum, * FROM (inner ORDER BY …)} ({@code select.md} §8.3 example —
	 * ORDER BY in subquery for stable ROWNUM sequence).
	 */
	public static String rownumOrderedWrapperSql(String orderedInnerSelect) {
		return "select rownum, * from (" + orderedInnerSelect + ")";
	}

	/**
	 * {@code SELECT rownum, * FROM table WHERE rownum < n} ({@code select.md} §8.3 filter example).
	 */
	public static String rownumFilterSql(String tableName, int maxRowExclusive) {
		return "select rownum, * from " + tableName + " where rownum < " + maxRowExclusive;
	}

	/**
	 * Oracle-style offset page via nested ROWNUM ({@code select.md} §8.3 subquery pattern).
	 *
	 * @param orderedInnerSelect inner query with {@code ORDER BY} when ordering matters
	 * @param maxRowInclusive    outer {@code rownum <= maxRowInclusive}
	 * @param offsetExclusive    outer {@code rn > offsetExclusive}
	 */
	public static String rownumPageSql(String orderedInnerSelect, int maxRowInclusive, int offsetExclusive) {
		return "select * from ("
				+ "select rownum rn, t.* from (" + orderedInnerSelect + ") t where rownum <= " + maxRowInclusive
				+ ") where rn > " + offsetExclusive;
	}
}
