package com.xugu.dialect.type;

import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.extract.spi.ColumnTypeInformation;
import org.hibernate.type.BasicType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeConstructor;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Constructs {@link XuguCastingJsonArrayJdbcType} for {@link SqlTypes#JSON_ARRAY}.
 */
public class XuguCastingJsonArrayJdbcTypeConstructor implements JdbcTypeConstructor {

	public static final XuguCastingJsonArrayJdbcTypeConstructor INSTANCE =
			new XuguCastingJsonArrayJdbcTypeConstructor();

	@Override
	public JdbcType resolveType(
			TypeConfiguration typeConfiguration,
			Dialect dialect,
			BasicType<?> elementType,
			ColumnTypeInformation columnTypeInformation) {
		return resolveType( typeConfiguration, dialect, elementType.getJdbcType(), columnTypeInformation );
	}

	@Override
	public JdbcType resolveType(
			TypeConfiguration typeConfiguration,
			Dialect dialect,
			JdbcType elementType,
			ColumnTypeInformation columnTypeInformation) {
		return new XuguCastingJsonArrayJdbcType( elementType );
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return SqlTypes.JSON_ARRAY;
	}
}
