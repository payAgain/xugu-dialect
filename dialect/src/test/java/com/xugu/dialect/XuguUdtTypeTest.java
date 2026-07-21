package com.xugu.dialect;

import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguUdtTypeSupport;
import com.xugu.dialect.type.XuguUdtTypeSupport.Kind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Offline unit tests: UDT type support (A-TYP-018).
 *
 * <p><b>Known-limit:</b> Hibernate 7.4 has no {@code SqlTypes} for XuGu UDT columns; dialect
 * locks documented CREATE/DROP TYPE shapes in {@link XuguUdtTypeSupport}. Live acceptance uses
 * native SQL round-trip IT — not ORM entity UDT attribute mapping.
 */
class XuguUdtTypeTest {

	@Test
	void documentedKindsLocked_A_TYP_018() {
		assertEquals( 3, XuguUdtTypeSupport.documentedKinds().length );
		assertEquals( Kind.OBJECT, XuguUdtTypeSupport.documentedKinds()[0] );
		assertEquals( Kind.VARRAY, XuguUdtTypeSupport.documentedKinds()[1] );
		assertEquals( Kind.TABLE, XuguUdtTypeSupport.documentedKinds()[2] );
		assertEquals( 65_535, XuguUdtTypeSupport.VARRAY_MAX_CAPACITY );
	}

	@Test
	void createTypeSqlMatchesUdtDoc_A_TYP_018() {
		assertEquals(
				"create or replace type udt_obj_type as object (n numeric, class varchar2, type varchar, dt date)",
				XuguUdtTypeSupport.createObjectTypeSql(
						"udt_obj_type",
						"n numeric, class varchar2, type varchar, dt date"
				)
		);
		assertEquals(
				"create or replace type var_test is varray(3) of varchar",
				XuguUdtTypeSupport.createVarrayTypeSql( "var_test", 3, "varchar" )
		);
		assertEquals(
				"create or replace type udt_tab_type is table of bigint",
				XuguUdtTypeSupport.createTableTypeSql( "udt_tab_type", "bigint" )
		);
		assertEquals( "drop type person", XuguUdtTypeSupport.dropTypeSql( "person" ) );
		assertEquals( "udt_obj_type", XuguUdtTypeSupport.columnTypeFor( "udt_obj_type" ) );
	}

	@Test
	void dialectDoesNotClaimOrmUdtEntityMapping_A_TYP_018() {
		assertFalse( new XuguDialect().supportsJdbcUserDefinedTypes(),
				"A-TYP-018 known-limit: no verified JDBC UDT entity mapping" );
	}
}
