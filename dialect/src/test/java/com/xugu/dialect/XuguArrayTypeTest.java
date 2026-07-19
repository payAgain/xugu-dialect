package com.xugu.dialect;

import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: ARRAY type hooks (A-TYP-015 / C-DDL-005).
 */
class XuguArrayTypeTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void arrayTypeHooksWired_A_TYP_015_C_DDL_005() {
		assertTrue( dialect.supportsStandardArrays() );
		assertEquals( SqlTypes.ARRAY, dialect.getPreferredSqlTypeCodeForArray() );
		assertTrue( dialect.supportsArrayConstructor() );
		assertEquals( "array", dialect.columnType( SqlTypes.ARRAY ) );
		assertEquals( "integer array", dialect.getArrayTypeName( "Integer", "integer", null ) );
	}
}
