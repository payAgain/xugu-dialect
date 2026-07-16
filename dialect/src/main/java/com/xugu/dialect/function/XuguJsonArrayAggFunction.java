package com.xugu.dialect.function;

import java.util.List;

import org.hibernate.dialect.function.json.JsonArrayAggFunction;
import org.hibernate.metamodel.model.domain.ReturnableType;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Distinct;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.predicate.Predicate;
import org.hibernate.sql.ast.tree.select.SortSpecification;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Renders XuGu-native {@code json_arrayagg(expr)} (C-JSON-001).
 *
 * <p>Docs: {@code reference/function/aggregate-functions/json_arrayagg.md} —
 * no returning / absent-on-null clauses.
 */
public class XuguJsonArrayAggFunction extends JsonArrayAggFunction {

	public XuguJsonArrayAggFunction(TypeConfiguration typeConfiguration) {
		super( false, typeConfiguration );
	}

	@Override
	public void render(
			SqlAppender sqlAppender,
			List<? extends SqlAstNode> sqlAstArguments,
			Predicate filter,
			List<SortSpecification> withinGroup,
			ReturnableType<?> returnType,
			SqlAstTranslator<?> translator) {
		sqlAppender.appendSql( "json_arrayagg(" );
		Expression arg = extractArgument( sqlAstArguments.get( 0 ) );
		arg.accept( translator );
		sqlAppender.appendSql( ')' );
	}

	private static Expression extractArgument(SqlAstNode node) {
		if ( node instanceof Distinct distinct ) {
			return distinct.getExpression();
		}
		return (Expression) node;
	}
}
