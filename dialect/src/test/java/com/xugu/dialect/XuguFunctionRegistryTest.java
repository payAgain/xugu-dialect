package com.xugu.dialect;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.dialect.function.ListaggFunction;
import org.hibernate.dialect.function.json.JsonValueFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.sqm.function.SqmFunctionDescriptor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.function.XuguFunctionRegistrations;
import com.xugu.dialect.function.XuguGeometricFunctions;
import com.xugu.dialect.support.OfflineConnectionProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: key function descriptors registered / pattern fragments (P-006).
 */
class XuguFunctionRegistryTest {

	private static StandardServiceRegistry registry;
	private static SessionFactoryImplementor sessionFactory;
	private static SqmFunctionRegistry functions;

	@BeforeAll
	static void bootOffline() {
		registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( JdbcSettings.ALLOW_METADATA_ON_BOOT, false )
				.applySetting( AvailableSettings.CONNECTION_PROVIDER, OfflineConnectionProvider.class.getName() )
				.build();
		SessionFactory sf = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
		sessionFactory = (SessionFactoryImplementor) sf;
		functions = sessionFactory.getQueryEngine().getSqmFunctionRegistry();
	}

	@AfterAll
	static void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
		if ( registry != null ) {
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Test
	void coreAnsiFunctionsRegistered() {
		assertRegistered( "concat" );
		assertRegistered( "substring" );
		assertRegistered( "substr" );
		assertRegistered( "length" );
		assertRegistered( "lower" );
		assertRegistered( "upper" );
		assertRegistered( "trim" );
		assertRegistered( "ltrim" );
		assertRegistered( "rtrim" );
		assertRegistered( "replace" );
		assertRegistered( "locate" );
		assertRegistered( "position" );
		assertRegistered( "coalesce" );
		assertRegistered( "nullif" );
		assertRegistered( "nvl" );
		assertRegistered( "abs" );
		assertRegistered( "mod" );
		assertRegistered( "power" );
		assertRegistered( "sqrt" );
		assertRegistered( "round" );
		assertRegistered( "floor" );
		assertRegistered( "ceil" );
		assertRegistered( "trunc" );
		assertRegistered( "current_date" );
		assertRegistered( "current_timestamp" );
		assertRegistered( "now" );
		assertRegistered( "extract" );
		assertRegistered( "year" );
		assertRegistered( "month" );
		assertRegistered( "day" );
		assertRegistered( "to_char" );
		assertRegistered( "to_date" );
		assertRegistered( "to_timestamp" );
		assertRegistered( "cast" );
		assertRegistered( "count" );
		assertRegistered( "sum" );
		assertRegistered( "avg" );
		assertRegistered( "min" );
		assertRegistered( "max" );
	}

	@Test
	void uuidPrimaryIsUuid() {
		assertEquals( "uuid", XuguFunctionRegistrations.PRIMARY_UUID_FUNCTION );
		assertRegistered( "uuid" );
		assertRegistered( "gen_random_uuid" );
		assertRegistered( "sys_guid" );
		SqmFunctionDescriptor uuid = functions.findFunctionDescriptor( "uuid" );
		assertNotNull( uuid );
		String sig = uuid.getSignature( "uuid" );
		assertTrue( sig == null || sig.toLowerCase().contains( "uuid" ) || uuid.toString().toLowerCase().contains( "uuid" ),
				"uuid descriptor pattern fragment, sig=" + sig + " desc=" + uuid );
	}

	@Test
	void jsonSubsetUsesStandardJsonValueNotMysqlDump() {
		SqmFunctionDescriptor jsonValue = functions.findFunctionDescriptor( "json_value" );
		assertNotNull( jsonValue );
		assertInstanceOf( JsonValueFunction.class, jsonValue );
		assertRegistered( "json_extract" );
		assertRegistered( "json_unquote" );
		assertRegistered( "json_length" );
		assertRegistered( "json_type" );
		// Documented subset only — do not register MySQL json_set as supported
		assertNull( functions.findFunctionDescriptor( "json_set" ),
				"json_set must not be registered (not in XuGu Hibernate subset)" );
	}

	@Test
	void listaggUsesNativeListaggFunction() {
		SqmFunctionDescriptor listagg = functions.findFunctionDescriptor( "listagg" );
		assertNotNull( listagg );
		assertInstanceOf( ListaggFunction.class, listagg );
		assertTrue( listagg.getClass().getSimpleName().toLowerCase().contains( "listagg" ) );
		assertRegistered( "string_agg" );
		assertRegistered( "group_concat" );
	}

	@Test
	void xmlSubsetRegistered_A_FUN_021() {
		assertRegistered( "xmlelement" );
		assertRegistered( "xmlquery" );
		assertRegistered( "xmltable" );
		// EXTRACT(XML,xpath) is native-SQL only — temporal extract(field from …) keeps Dialect default
		assertNotNull( functions.findFunctionDescriptor( "extract" ) );
		assertNull( functions.findFunctionDescriptor( "extractvalue" ),
				"extractvalue not in bounded A-FUN-021 subset" );
	}

	@Test
	void geometricSubsetRegistered_A_FUN_020() {
		for ( String name : XuguGeometricFunctions.DOCUMENTED_NAMES ) {
			assertRegistered( name );
		}
		assertEquals( 21, XuguGeometricFunctions.DOCUMENTED_NAMES.size() );
	}

	@Test
	void unsupportedFunctionNotRegistered_negativeNote() {
		// Diagnosability: unregistered name has no descriptor; HQL failure covered in gated IT.
		assertNull( functions.findFunctionDescriptor( "xugu_unsupported_fn_xyz" ) );
		assertNull( functions.findFunctionDescriptor( "bit_and" ),
				"A-FUN-015 bit_and deferred — must not appear as registered support" );
	}

	private static void assertRegistered(String name) {
		assertNotNull( functions.findFunctionDescriptor( name ), "missing function: " + name );
	}
}
