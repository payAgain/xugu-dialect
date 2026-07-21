package com.xugu.dialect.function;

import java.util.List;

/**
 * Documented XuGu regexp function names ({@code reference/function/string-functions/regexp_*.md}).
 *
 * <p>Bounded A-FUN-019 subset — {@code regexp_like}, {@code regexp_replace},
 * {@code regexp_substr} per matrix scope; native SQL IT uses doc examples only.
 */
public final class XuguRegexpFunctions {

	private XuguRegexpFunctions() {
	}

	/** Documented regexp functions in P-006 scope (3). */
	public static final List<String> DOCUMENTED_NAMES = List.of(
			"regexp_like",
			"regexp_replace",
			"regexp_substr"
	);
}
