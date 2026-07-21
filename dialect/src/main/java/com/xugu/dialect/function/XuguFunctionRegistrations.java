package com.xugu.dialect.function;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.function.CommonFunctionFactory;
import org.hibernate.dialect.function.json.JsonValueFunction;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.query.sqm.produce.function.FunctionParameterType;
import org.hibernate.query.sqm.produce.function.StandardFunctionArgumentTypeResolvers;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * XuGu-native SQL function contributions for Hibernate 7.4 (P-006 / A-FUN-*).
 *
 * <p>Primary UUID SQL form: {@code uuid()} — returns dashed VARCHAR UUID; proven on live
 * XuguDB. Alternates {@code gen_random_uuid()} / {@code sys_guid()} are also registered
 * (documented) but {@code uuid()} is the dialect primary.
 *
 * <p>JSON: {@code json_value} + {@code json_extract}, plus native
 * {@code json_arrayagg}/{@code json_objectagg} (I-003 C-JSON-001/002).
 * C-JSON-005 bounded deepen: {@code json_unquote}, {@code json_length}, {@code json_type}
 * ({@code reference/function/json-functions/**}) — not the full {@code XuguJsonFunctions} set.
 * HQL JSON functions require {@code hibernate.query.hql.json_functions_enabled=true}.
 *
 * <p>String aggregate: Hibernate {@code listagg} → native XuGu
 * {@code LISTAGG(...) WITHIN GROUP (ORDER BY ...)}.
 *
 * <p>XML (A-FUN-021): bounded subset {@code xmlelement}/{@code xmlquery}/{@code xmltable}
 * ({@code reference/function/xml-functions/**}). HQL Session live positive for
 * {@code xmlelement}/{@code xmlquery} (I-010/P-005); {@code xmlquery} renders
 * {@code xmlquery(?1 PASSING ?2 RETURNING CONTENT)}. {@code XMLTABLE} remains
 * native-SQL IT with empty→assumption skip (single-node known-limit — not covered-live).
 * {@code EXTRACT(XML,xpath)} is native SQL IT only — temporal {@code extract(field from …)}
 * remains the Dialect default.
 *
 * <p>Geometric (A-FUN-020): all 21 documented functions in
 * {@code reference/function/geometric-functions/} — paired with A-TYP-017 literals; native
 * SQL IT for representative calls ({@code area}/{@code center}/{@code point}/{@code box}/
 * {@code circle}).
 *
 * <p>Regexp (A-FUN-019): {@code regexp_like}/{@code regexp_replace}/{@code regexp_substr}
 * ({@code reference/function/string-functions/regexp_*.md}).
 *
 * <p>Bit aggregates (A-FUN-015): {@code bit_and}/{@code bit_or} on {@code VARBIT}
 * ({@code reference/function/aggregate-functions/bit_*.md}); native SQL IT only — no VARBIT ORM hook.
 */
public final class XuguFunctionRegistrations {

	/** Primary UUID generator SQL name for this dialect (live-proven). */
	public static final String PRIMARY_UUID_FUNCTION = "uuid";

	private XuguFunctionRegistrations() {
	}

	public static void register(FunctionContributions contributions) {
		final TypeConfiguration typeConfiguration = contributions.getTypeConfiguration();
		final SqmFunctionRegistry functionRegistry = contributions.getFunctionRegistry();
		final CommonFunctionFactory functionFactory = new CommonFunctionFactory( contributions );
		final BasicType<String> stringType = typeConfiguration.getBasicTypeRegistry()
				.resolve( StandardBasicTypes.STRING );

		// --- String (A-FUN-001..006 extras beyond Dialect defaults) ---
		// concat / substring / lower / upper / length / replace / locate / trim:
		// already registered by Dialect.initializeFunctionRegistry defaults.
		functionFactory.substr(); // A-FUN-002 native substr
		functionFactory.position(); // A-FUN-006 ANSI POSITION
		functionFactory.trim1(); // A-FUN-005 ltrim / rtrim

		// --- Null-handling (A-FUN-007): prefer COALESCE (already from Dialect); add NVL ---
		functionRegistry.namedDescriptorBuilder( "nvl" )
				.setExactArgumentCount( 2 )
				.setArgumentTypeResolver( StandardFunctionArgumentTypeResolvers.ARGUMENT_OR_IMPLIED_RESULT_TYPE )
				.register();

		// --- Math (A-FUN-008/009) ---
		functionFactory.ceiling_ceil(); // ceil + ceiling alias
		functionFactory.trunc(); // trunc / truncate numeric

		// --- Temporal (A-FUN-010/011/012) ---
		functionFactory.nowCurdateCurtime(); // now() / curdate() / curtime()
		functionFactory.yearMonthDay(); // year / month / day
		functionFactory.toCharNumberDateTimestamp(); // to_char / to_date / to_timestamp

		// --- UUID (A-FUN-016): primary = uuid() ---
		functionRegistry.noArgsBuilder( PRIMARY_UUID_FUNCTION )
				.setInvariantType( stringType )
				.setUseParenthesesWhenNoArgs( true )
				.register();
		functionRegistry.noArgsBuilder( "gen_random_uuid" )
				.setInvariantType( stringType )
				.setUseParenthesesWhenNoArgs( true )
				.register();
		functionRegistry.noArgsBuilder( "sys_guid" )
				.setInvariantType( stringType )
				.setUseParenthesesWhenNoArgs( true )
				.register();

		// --- JSON subset (A-FUN-017 + C-JSON-001/002) ---
		// Base JsonValueFunction renders JSON_VALUE(doc, path …) matching XuGu docs.
		// Path expression supported; PASSING clause not documented for XuGu → false.
		functionRegistry.register(
				"json_value",
				new JsonValueFunction( typeConfiguration, true, false )
		);
		functionRegistry.namedDescriptorBuilder( "json_extract" )
				.setMinArgumentCount( 2 )
				.setParameterTypes( FunctionParameterType.IMPLICIT_JSON, FunctionParameterType.STRING )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(JSON jsonDoc, STRING path[, STRING path…])" )
				.register();
		functionRegistry.register( "json_arrayagg", new XuguJsonArrayAggFunction( typeConfiguration ) );
		functionRegistry.register( "json_objectagg", new XuguJsonObjectAggFunction( typeConfiguration ) );

		// --- C-JSON-005 deepen subset (docs: reference/function/json-functions/**) ---
		functionRegistry.namedDescriptorBuilder( "json_unquote" )
				.setMinArgumentCount( 1 )
				.setParameterTypes( FunctionParameterType.IMPLICIT_JSON )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(JSON jsonDoc[, STRING path…])" )
				.register();
		functionRegistry.namedDescriptorBuilder( "json_length" )
				.setMinArgumentCount( 1 )
				.setParameterTypes( FunctionParameterType.IMPLICIT_JSON, FunctionParameterType.STRING )
				.setInvariantType( typeConfiguration.getBasicTypeRegistry().resolve( StandardBasicTypes.INTEGER ) )
				.setArgumentListSignature( "(JSON jsonDoc[, STRING path])" )
				.register();
		functionRegistry.namedDescriptorBuilder( "json_type" )
				.setMinArgumentCount( 1 )
				.setParameterTypes( FunctionParameterType.IMPLICIT_JSON, FunctionParameterType.STRING )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(JSON jsonDoc[, STRING path])" )
				.register();

		// --- XML subset (A-FUN-021) — docs: reference/function/xml-functions/** ---
		// HQL Session positive path (I-010/P-005): xmlelement + xmlquery.
		// XuGu XMLELEMENT requires double-quoted xmlname (not a string literal).
		// xmlquery must emit PASSING … RETURNING CONTENT (xmlquery.md), not csv args.
		functionRegistry.register( "xmlelement", new XuguXmlElementFunction( typeConfiguration ) );
		functionRegistry.patternDescriptorBuilder(
						"xmlquery",
						"xmlquery(?1 PASSING ?2 RETURNING CONTENT)" )
				.setExactArgumentCount( 2 )
				.setParameterTypes( FunctionParameterType.STRING, FunctionParameterType.IMPLICIT_XML )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(STRING xpath, XML xmlData)" )
				.register();
		// xmltable: registered for name discovery; live IT uses native SQL only.
		// XMLTABLE is single-node / known-limit — never claim HQL covered-live.
		functionRegistry.namedDescriptorBuilder( "xmltable" )
				.setMinArgumentCount( 1 )
				.setParameterTypes( FunctionParameterType.STRING )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(XQuery PASSING XML_data COLUMNS …)" )
				.register();

		// --- Geometric subset (A-FUN-020) — docs: reference/function/geometric-functions/** ---
		registerGeometricFunctions( functionRegistry, stringType, typeConfiguration );

		// --- Regexp subset (A-FUN-019) — docs: reference/function/string-functions/regexp_*.md ---
		registerRegexpFunctions( functionRegistry, stringType, typeConfiguration );

		// --- Bit aggregates (A-FUN-015) — docs: reference/function/aggregate-functions/bit_*.md ---
		registerBitAggregateFunctions( functionRegistry, stringType );

		// --- listagg / string_agg / group_concat (A-FUN-018) ---
		// Hibernate HQL listagg → native LISTAGG … WITHIN GROUP (XuGu form).
		functionFactory.listagg( null );
		functionRegistry.namedDescriptorBuilder( "string_agg" )
				.setArgumentCountBetween( 2, 3 )
				.setParameterTypes( FunctionParameterType.STRING, FunctionParameterType.STRING )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(STRING expr, STRING separator[, order by…])" )
				.register();
		functionRegistry.namedDescriptorBuilder( "group_concat" )
				.setMinArgumentCount( 1 )
				.setParameterTypes( FunctionParameterType.STRING )
				.setInvariantType( stringType )
				.setArgumentListSignature( "(STRING expr[, …] [ORDER BY …] [SEPARATOR delimiter])" )
				.register();
	}

	private static void registerRegexpFunctions(
			SqmFunctionRegistry functionRegistry,
			BasicType<String> stringType,
			TypeConfiguration typeConfiguration) {
		final BasicType<Boolean> booleanType = typeConfiguration.getBasicTypeRegistry()
				.resolve( StandardBasicTypes.BOOLEAN );
		final BasicType<Integer> integerType = typeConfiguration.getBasicTypeRegistry()
				.resolve( StandardBasicTypes.INTEGER );

		functionRegistry.namedDescriptorBuilder( "regexp_like" )
				.setArgumentCountBetween( 2, 3 )
				.setParameterTypes( FunctionParameterType.STRING, FunctionParameterType.STRING )
				.setInvariantType( booleanType )
				.setArgumentListSignature( "(STRING expr, STRING pattern[, STRING match_mode])" )
				.register();
		functionRegistry.namedDescriptorBuilder( "regexp_replace" )
				.setArgumentCountBetween( 2, 6 )
				.setParameterTypes(
						FunctionParameterType.STRING,
						FunctionParameterType.STRING,
						FunctionParameterType.STRING,
						FunctionParameterType.INTEGER,
						FunctionParameterType.INTEGER,
						FunctionParameterType.STRING
				)
				.setInvariantType( stringType )
				.setArgumentListSignature(
						"(STRING expr, STRING pattern[, STRING replacement[, INTEGER start[, INTEGER occurrence[, STRING match_mode]]]])"
				)
				.register();
		functionRegistry.namedDescriptorBuilder( "regexp_substr" )
				.setArgumentCountBetween( 2, 5 )
				.setParameterTypes(
						FunctionParameterType.STRING,
						FunctionParameterType.STRING,
						FunctionParameterType.INTEGER,
						FunctionParameterType.INTEGER,
						FunctionParameterType.STRING
				)
				.setInvariantType( stringType )
				.setArgumentListSignature(
						"(STRING expr, STRING pattern[, INTEGER start[, INTEGER occurrence[, STRING match_mode]]])"
				)
				.register();
	}

	private static void registerBitAggregateFunctions(
			SqmFunctionRegistry functionRegistry,
			BasicType<String> stringType) {
		for ( String name : XuguBitAggregateFunctions.DOCUMENTED_NAMES ) {
			functionRegistry.namedDescriptorBuilder( name )
					.setExactArgumentCount( 1 )
					.setInvariantType( stringType )
					.setArgumentListSignature( "(VARBIT expr)" )
					.register();
		}
	}

	private static void registerGeometricFunctions(
			SqmFunctionRegistry functionRegistry,
			BasicType<String> stringType,
			TypeConfiguration typeConfiguration) {
		final BasicType<Integer> integerType = typeConfiguration.getBasicTypeRegistry()
				.resolve( StandardBasicTypes.INTEGER );
		final BasicType<Double> doubleType = typeConfiguration.getBasicTypeRegistry()
				.resolve( StandardBasicTypes.DOUBLE );
		final BasicType<Boolean> booleanType = typeConfiguration.getBasicTypeRegistry()
				.resolve( StandardBasicTypes.BOOLEAN );

		for ( String name : XuguGeometricFunctions.DOCUMENTED_NAMES ) {
			functionRegistry.namedDescriptorBuilder( name )
					.setMinArgumentCount( 1 )
					.setInvariantType( geometricReturnType( name, stringType, integerType, doubleType, booleanType ) )
					.setArgumentListSignature( geometricSignature( name ) )
					.register();
		}
	}

	private static BasicType<?> geometricReturnType(
			String name,
			BasicType<String> stringType,
			BasicType<Integer> integerType,
			BasicType<Double> doubleType,
			BasicType<Boolean> booleanType) {
		return switch ( name ) {
			case "area", "radius", "diameter", "height", "width", "slope" -> doubleType;
			case "npoints" -> integerType;
			case "isclosed", "isopen" -> booleanType;
			default -> stringType;
		};
	}

	private static String geometricSignature(String name) {
		return switch ( name ) {
			case "bound_box" -> "(BOX box1, BOX box2)";
			case "box", "circle", "line", "lseg", "point", "polygon" -> "(expr1[, expr2])";
			case "popen", "pclose" -> "(PATH path)";
			default -> "(expr)";
		};
	}
}
