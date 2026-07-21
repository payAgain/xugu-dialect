package com.xugu.dialect.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcLiteralFormatter;
import org.hibernate.type.descriptor.jdbc.JdbcType;

/**
 * XuGu POINT JDBC binding for Hibernate {@link SqlTypes#POINT} / {@link SqlTypes#GEOMETRY}.
 *
 * <p>Native IT and {@code reference/sql/datatype/geometric.md} show XuGu POINT columns as
 * string-valued literals {@code (x,y)}. Hibernate core has no default {@code PointJdbcType};
 * spatial ORM typically requires hibernate-spatial. This type binds and extracts with
 * {@link PreparedStatement#setString}/{@link ResultSet#getString} while keeping DDL on
 * {@code POINT} via {@link XuguGeometricTypeSupport}.
 *
 * <p>{@link SqlTypes#GEOMETRY} is contributed to the same binder because the dialect maps
 * GEOMETRY → bounded POINT DDL (XuGu has no PostGIS generic GEOMETRY).
 *
 * <p><b>Recommended mapping:</b> {@code String} attribute +
 * {@code @JdbcTypeCode(SqlTypes.POINT)} (or {@code GEOMETRY} for the bounded alias).
 * Non-POINT subtypes (LINE/LSEG/BOX/PATH/POLYGON/CIRCLE) remain native/tooling only.
 */
public final class XuguPointJdbcType implements JdbcType {

	public static final XuguPointJdbcType POINT = new XuguPointJdbcType( SqlTypes.POINT );
	public static final XuguPointJdbcType GEOMETRY = new XuguPointJdbcType( SqlTypes.GEOMETRY );

	private final int sqlTypeCode;

	private XuguPointJdbcType(int sqlTypeCode) {
		this.sqlTypeCode = sqlTypeCode;
	}

	@Override
	public int getJdbcTypeCode() {
		return Types.VARCHAR;
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return sqlTypeCode;
	}

	@Override
	public Class<?> getPreferredJavaTypeClass(WrapperOptions options) {
		return String.class;
	}

	@Override
	public String toString() {
		return sqlTypeCode == SqlTypes.POINT
				? "XuguPointJdbcType(POINT)"
				: "XuguPointJdbcType(GEOMETRY)";
	}

	@Override
	public <T> JdbcLiteralFormatter<T> getJdbcLiteralFormatter(JavaType<T> javaType) {
		return (appender, value, dialect, wrapperOptions) -> {
			final String point = javaType.unwrap( value, String.class, wrapperOptions );
			appender.appendSql( '\'' );
			if ( point != null ) {
				appender.appendSql( point.replace( "'", "''" ) );
			}
			appender.appendSql( '\'' );
		};
	}

	@Override
	public <X> ValueBinder<X> getBinder(JavaType<X> javaType) {
		return new BasicBinder<>( javaType, this ) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
					throws SQLException {
				st.setString( index, javaType.unwrap( value, String.class, options ) );
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				st.setString( name, javaType.unwrap( value, String.class, options ) );
			}
		};
	}

	@Override
	public <X> ValueExtractor<X> getExtractor(JavaType<X> javaType) {
		return new BasicExtractor<>( javaType, this ) {
			@Override
			protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
				return javaType.wrap( normalize( rs.getString( paramIndex ) ), options );
			}

			@Override
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
					throws SQLException {
				return javaType.wrap( normalize( statement.getString( index ) ), options );
			}

			@Override
			protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
					throws SQLException {
				return javaType.wrap( normalize( statement.getString( name ) ), options );
			}
		};
	}

	/** Trim JDBC trailing whitespace; POINT literals are string-valued on XuGu. */
	public static String normalize(String raw) {
		return raw == null ? null : raw.trim();
	}
}
