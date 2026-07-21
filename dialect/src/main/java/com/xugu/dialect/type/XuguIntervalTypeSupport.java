package com.xugu.dialect.type;

/**
 * XuGu INTERVAL subtype DDL ({@code reference/sql/datatype/datetime.md} §时间间隔类型).
 *
 * <p><b>A-TYP-014:</b> XuGu documents <strong>13</strong> INTERVAL subtypes plus
 * {@code DEF_INTERVAL_STYLE} output formats; Hibernate 7.4 exposes only
 * {@code SqlTypes.DURATION} and {@code SqlTypes.INTERVAL_SECOND} — not per-subtype ORM codes.
 * This class locks documented DDL strings for schema tooling and native SQL. Entity ORM
 * round-trip for the two Hibernate codes uses {@link XuguIntervalJdbcType}; the other 11
 * subtypes remain tooling / native-SQL only.
 */
public final class XuguIntervalTypeSupport {

	private XuguIntervalTypeSupport() {
	}

	/** Hibernate {@code SqlTypes.DURATION} → documented day-time span ({@code INTERVAL DAY TO SECOND}). */
	public static final String DURATION_DDL = "interval day to second";

	/** Hibernate {@code SqlTypes.INTERVAL_SECOND}. */
	public static final String INTERVAL_SECOND_DDL = "interval second";

	/**
	 * Documented XuGu INTERVAL subtypes ({@code datetime.md} syntax table).
	 */
	public enum Subtype {
		YEAR( "interval year" ),
		MONTH( "interval month" ),
		DAY( "interval day" ),
		HOUR( "interval hour" ),
		MINUTE( "interval minute" ),
		SECOND( "interval second" ),
		YEAR_TO_MONTH( "interval year to month" ),
		DAY_TO_HOUR( "interval day to hour" ),
		DAY_TO_MINUTE( "interval day to minute" ),
		DAY_TO_SECOND( "interval day to second" ),
		HOUR_TO_MINUTE( "interval hour to minute" ),
		HOUR_TO_SECOND( "interval hour to second" ),
		MINUTE_TO_SECOND( "interval minute to second" );

		private final String ddlBase;

		Subtype(String ddlBase) {
			this.ddlBase = ddlBase;
		}

		public String ddlBase() {
			return ddlBase;
		}
	}

	public static String ddlFor(Subtype subtype) {
		return subtype.ddlBase();
	}

	public static Subtype[] documentedSubtypes() {
		return Subtype.values();
	}
}
