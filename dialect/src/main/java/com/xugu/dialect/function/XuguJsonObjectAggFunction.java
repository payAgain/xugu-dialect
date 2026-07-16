package com.xugu.dialect.function;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.json.JsonObjectAggFunction;
import org.hibernate.metamodel.model.domain.ReturnableType;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.JsonNullBehavior;
import org.hibernate.sql.ast.tree.expression.JsonObjectAggUniqueKeysBehavior;
import org.hibernate.sql.ast.tree.predicate.Predicate;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Renders XuGu-native {@code json_objectagg(key, value)} (C-JSON-002).
 *
 * <p>Docs: {@code reference/function/aggregate-functions/json_objectagg.md}.
 */
public class XuguJsonObjectAggFunction extends JsonObjectAggFunction {

	public XuguJsonObjectAggFunction(TypeConfiguration typeConfiguration) {
		super( ", ", false, typeConfiguration );
	}

	@Override
	protected void render(
			SqlAppender sqlAppender,
			JsonObjectAggArguments arguments,
			Predicate filter,
			ReturnableType<?> returnType,
			SqlAstTranslator<?> translator) {
		if ( filter != null ) {
			throw new QueryException( "Xugu json_objectagg does not support filter clause" );
		}
		if ( arguments.uniqueKeysBehavior() == JsonObjectAggUniqueKeysBehavior.WITH ) {
			throw new QueryException( "Xugu json_objectagg does not support 'with unique keys'" );
		}
		sqlAppender.appendSql( "json_objectagg(" );
		arguments.key().accept( translator );
		sqlAppender.appendSql( ", " );
		renderArgument( sqlAppender, arguments.value(), arguments.nullBehavior(), translator );
		sqlAppender.appendSql( ')' );
	}

	@Override
	protected void renderArgument(
			SqlAppender sqlAppender,
			Expression arg,
			JsonNullBehavior nullBehavior,
			SqlAstTranslator<?> translator) {
		arg.accept( translator );
	}
}
