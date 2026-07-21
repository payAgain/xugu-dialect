package com.xugu.dialect.function;

import java.util.List;

import org.hibernate.metamodel.model.domain.ReturnableType;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionArgumentTypeResolvers;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Renders XuGu {@code XMLELEMENT(xmlname[, xmlvalue])} for HQL.
 *
 * <p>XuGu requires the element name as a double-quoted identifier
 * ({@code XMLELEMENT("name",'xxx')}); a string-literal first argument
 * ({@code XMLELEMENT('name','xxx')}) is a syntax error.
 */
public final class XuguXmlElementFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

	public XuguXmlElementFunction(TypeConfiguration typeConfiguration) {
		super(
				"xmlelement",
				StandardArgumentsValidators.min( 1 ),
				StandardFunctionReturnTypeResolvers.invariant(
						typeConfiguration.getBasicTypeRegistry().resolve( StandardBasicTypes.STRING ) ),
				StandardFunctionArgumentTypeResolvers.invariant(
						typeConfiguration,
						org.hibernate.query.sqm.produce.function.FunctionParameterType.STRING ) );
	}

	@Override
	public void render(
			SqlAppender sqlAppender,
			List<? extends SqlAstNode> sqlAstArguments,
			ReturnableType<?> returnType,
			SqlAstTranslator<?> walker) {
		sqlAppender.appendSql( "xmlelement(" );
		renderXmlName( sqlAppender, sqlAstArguments.get( 0 ), walker );
		for ( int i = 1; i < sqlAstArguments.size(); i++ ) {
			sqlAppender.appendSql( ',' );
			sqlAstArguments.get( i ).accept( walker );
		}
		sqlAppender.appendSql( ')' );
	}

	private static void renderXmlName(
			SqlAppender sqlAppender,
			SqlAstNode nameNode,
			SqlAstTranslator<?> walker) {
		if ( nameNode instanceof QueryLiteral<?> literal ) {
			Object value = literal.getLiteralValue();
			if ( value instanceof String name ) {
				sqlAppender.appendSql( '"' );
				sqlAppender.appendSql( name.replace( "\"", "\"\"" ) );
				sqlAppender.appendSql( '"' );
				return;
			}
		}
		if ( nameNode instanceof Expression expression ) {
			expression.accept( walker );
			return;
		}
		nameNode.accept( walker );
	}
}
