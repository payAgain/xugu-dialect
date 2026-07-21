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
 * XuGu XML JDBC binding for Hibernate {@link SqlTypes#SQLXML}.
 *
 * <p>Native IT and {@code reference/sql/datatype/xml.md} show XuGu XML columns as
 * string-valued. Standard Hibernate {@code XmlJdbcType} binds via
 * {@code java.sql.SQLXML} ({@code Connection#createSQLXML} / {@code ResultSet#getSQLXML}),
 * which is unproven on Xugu JDBC. This type binds and extracts with
 * {@link PreparedStatement#setString}/{@link ResultSet#getString} while keeping DDL on
 * {@code SqlTypes.SQLXML} → {@link XuguXmlTypeSupport#XML_DDL}.
 *
 * <p><b>Recommended mapping:</b> {@code String} attribute +
 * {@code @JdbcTypeCode(SqlTypes.SQLXML)}. Do not claim a verified {@code java.sql.SQLXML}
 * entity path.
 */
public final class XuguXmlJdbcType implements JdbcType {

	public static final XuguXmlJdbcType INSTANCE = new XuguXmlJdbcType();

	private XuguXmlJdbcType() {
	}

	@Override
	public int getJdbcTypeCode() {
		return Types.VARCHAR;
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return SqlTypes.SQLXML;
	}

	@Override
	public Class<?> getPreferredJavaTypeClass(WrapperOptions options) {
		return String.class;
	}

	@Override
	public String toString() {
		return "XuguXmlJdbcType";
	}

	@Override
	public <T> JdbcLiteralFormatter<T> getJdbcLiteralFormatter(JavaType<T> javaType) {
		return (appender, value, dialect, wrapperOptions) -> {
			final String xml = javaType.unwrap( value, String.class, wrapperOptions );
			appender.appendSql( '\'' );
			if ( xml != null ) {
				appender.appendSql( xml.replace( "'", "''" ) );
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

	/** Trim JDBC trailing whitespace seen on some XuGu XML reads (native IT uses {@code trim()}). */
	public static String normalize(String raw) {
		return raw == null ? null : raw.trim();
	}
}
