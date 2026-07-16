package com.xugu.dialect.type;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.metamodel.mapping.EmbeddableMappingType;
import org.hibernate.metamodel.spi.RuntimeModelCreationContext;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.type.descriptor.jdbc.AggregateJdbcType;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;

/**
 * JSON JDBC type that writes {@code cast(? as json)} per XuGu JSON datatype docs (C-JSON-004).
 */
public class XuguCastingJsonJdbcType extends JsonJdbcType {

	public static final JsonJdbcType INSTANCE = new XuguCastingJsonJdbcType( null );

	public XuguCastingJsonJdbcType(EmbeddableMappingType embeddableMappingType) {
		super( embeddableMappingType );
	}

	@Override
	public AggregateJdbcType resolveAggregateJdbcType(
			EmbeddableMappingType mappingType,
			String sqlType,
			RuntimeModelCreationContext creationContext) {
		return new XuguCastingJsonJdbcType( mappingType );
	}

	@Override
	public void appendWriteExpression(String writeExpression, Size size, SqlAppender appender, Dialect dialect) {
		appender.append( "cast(" );
		appender.append( writeExpression );
		appender.append( " as json)" );
	}

	@Override
	public boolean isWriteExpressionTyped(Dialect dialect) {
		return true;
	}
}
