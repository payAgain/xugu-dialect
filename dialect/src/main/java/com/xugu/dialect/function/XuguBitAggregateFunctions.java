package com.xugu.dialect.function;

import java.util.List;

/**
 * Documented XuGu bit aggregate names ({@code reference/function/aggregate-functions/bit_*.md}).
 *
 * <p>A-FUN-015 — input {@code VARBIT}; Hibernate 7.4 has no {@code SqlTypes} for VARBIT entity
 * columns, so registry + native SQL IT is the honest path (HQL on entity columns = known-limit).
 */
public final class XuguBitAggregateFunctions {

	private XuguBitAggregateFunctions() {
	}

	/** Documented bit aggregates (2). */
	public static final List<String> DOCUMENTED_NAMES = List.of(
			"bit_and",
			"bit_or"
	);
}
