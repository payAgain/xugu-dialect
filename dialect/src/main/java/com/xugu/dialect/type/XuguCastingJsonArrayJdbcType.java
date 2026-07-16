package com.xugu.dialect.type;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JsonArrayJdbcType;

/**
 * JSON array JDBC type with {@code cast(? as json)} write expression (C-JSON-004).
 */
public class XuguCastingJsonArrayJdbcType extends JsonArrayJdbcType {

	public XuguCastingJsonArrayJdbcType(JdbcType elementJdbcType) {
		super( elementJdbcType );
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
