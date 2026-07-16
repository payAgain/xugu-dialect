package com.xugu.dialect.aggregate;

import org.hibernate.dialect.aggregate.AggregateSupport;
import org.hibernate.dialect.aggregate.AggregateSupportImpl;
import org.hibernate.mapping.Column;
import org.hibernate.metamodel.mapping.SqlTypedMapping;
import org.hibernate.type.spi.TypeConfiguration;

import static org.hibernate.dialect.function.array.DdlTypeHelper.getCastTypeName;
import static org.hibernate.type.SqlTypes.BOOLEAN;
import static org.hibernate.type.SqlTypes.JSON;
import static org.hibernate.type.SqlTypes.JSON_ARRAY;

/**
 * Aggregate (JSON embeddable) component read/write support for XuGu (C-JSON-003).
 *
 * <p>Uses documented {@code json_extract} / {@code json_unquote} paths.
 */
public class XuguAggregateSupport extends AggregateSupportImpl {

	public static final AggregateSupport INSTANCE = new XuguAggregateSupport();

	private XuguAggregateSupport() {
	}

	@Override
	public String aggregateComponentCustomReadExpression(
			String template,
			String placeholder,
			String aggregateParentReadExpression,
			String columnExpression,
			int aggregateColumnTypeCode,
			SqlTypedMapping column,
			TypeConfiguration typeConfiguration) {
		if ( aggregateColumnTypeCode != JSON && aggregateColumnTypeCode != JSON_ARRAY ) {
			throw new IllegalArgumentException( "Unsupported aggregate SQL type: " + aggregateColumnTypeCode );
		}
		final String path = "nullif(json_extract(" + aggregateParentReadExpression
				+ ",'$." + columnExpression + "'),cast('null' as json))";
		return switch ( column.getJdbcMapping().getJdbcType().getDefaultSqlTypeCode() ) {
			case JSON, JSON_ARRAY -> template.replace( placeholder, path );
			case BOOLEAN -> template.replace(
					placeholder,
					"case " + path
							+ " when cast('true' as json) then true when cast('false' as json) then false end"
			);
			default -> template.replace(
					placeholder,
					"cast(json_unquote(" + path + ") as "
							+ getCastTypeName( column.getJdbcMapping(), typeConfiguration ) + ')'
			);
		};
	}

	@Override
	public String aggregateComponentAssignmentExpression(
			String aggregateParentAssignmentExpression,
			String columnExpression,
			int aggregateColumnTypeCode,
			Column column) {
		if ( aggregateColumnTypeCode == JSON || aggregateColumnTypeCode == JSON_ARRAY ) {
			return aggregateParentAssignmentExpression;
		}
		throw new IllegalArgumentException( "Unsupported aggregate SQL type: " + aggregateColumnTypeCode );
	}

	@Override
	public boolean requiresAggregateCustomWriteExpressionRenderer(int aggregateSqlTypeCode) {
		return aggregateSqlTypeCode == JSON;
	}
}
