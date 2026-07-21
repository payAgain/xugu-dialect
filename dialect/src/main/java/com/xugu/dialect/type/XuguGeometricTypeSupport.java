package com.xugu.dialect.type;

/**
 * XuGu simple 2D geometric type DDL ({@code reference/sql/datatype/geometric.md}).
 *
 * <p><b>A-TYP-017:</b> XuGu documents seven native types — {@code POINT},
 * {@code LINE}, {@code LSEG}, {@code BOX}, {@code PATH}, {@code POLYGON}, {@code CIRCLE}
 * — with literal syntax only (not PostGIS {@code GEOMETRY}). Hibernate 7.4 exposes
 * {@code SqlTypes.POINT} and {@code SqlTypes.GEOMETRY} only; dialect maps both to
 * documented {@code POINT} DDL. {@link XuguPointJdbcType} contributes string-based JDBC
 * binding for entity {@code String} + {@code @JdbcTypeCode(POINT|GEOMETRY)} ORM round-trip.
 * Remaining subtypes (LINE/LSEG/BOX/PATH/POLYGON/CIRCLE) stay native SQL / schema tooling
 * only — <em>known-limit-documented</em> without inventing ORM JDBC descriptors for them.
 */
public final class XuguGeometricTypeSupport {

	private XuguGeometricTypeSupport() {
	}

	/** Hibernate {@code SqlTypes.POINT} → documented XuGu {@code POINT} column type. */
	public static final String POINT_DDL = "point";

	/**
	 * Bounded default when Hibernate emits {@code SqlTypes.GEOMETRY} — XuGu has no generic
	 * GEOMETRY/PostGIS type; {@code POINT} is the documented base construct.
	 */
	public static final String GEOMETRY_DDL = POINT_DDL;

	/**
	 * Documented XuGu simple geometric types ({@code geometric.md} table).
	 */
	public enum Kind {
		POINT( "point" ),
		LINE( "line" ),
		LSEG( "lseg" ),
		BOX( "box" ),
		PATH( "path" ),
		POLYGON( "polygon" ),
		CIRCLE( "circle" );

		private final String ddl;

		Kind(String ddl) {
			this.ddl = ddl;
		}

		public String ddl() {
			return ddl;
		}
	}

	public static String ddlFor(Kind kind) {
		return kind.ddl();
	}

	public static Kind[] documentedKinds() {
		return Kind.values();
	}
}
