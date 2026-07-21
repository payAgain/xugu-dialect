package com.xugu.dialect.function;

import java.util.List;

/**
 * Documented XuGu geometric function names ({@code reference/function/geometric-functions/}).
 *
 * <p>Bounded A-FUN-020 subset — all 21 doc files; native SQL IT exercises representative
 * calls; HQL registry uses lowercase names matching XuGu SQL.
 */
public final class XuguGeometricFunctions {

	private XuguGeometricFunctions() {
	}

	/** All documented geometric functions (21). */
	public static final List<String> DOCUMENTED_NAMES = List.of(
			"area",
			"bound_box",
			"box",
			"center",
			"circle",
			"diameter",
			"diagonal",
			"height",
			"isclosed",
			"isopen",
			"line",
			"lseg",
			"npoints",
			"path",
			"point",
			"polygon",
			"popen",
			"pclose",
			"radius",
			"slope",
			"width"
	);
}
