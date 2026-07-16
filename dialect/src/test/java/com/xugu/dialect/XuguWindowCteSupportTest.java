package com.xugu.dialect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class XuguWindowCteSupportTest {

	@Test
	void dialectEnablesWindowAndWithClause() {
		XuguDialect dialect = new XuguDialect();
		assertTrue( dialect.supportsWindowFunctions() );
		assertTrue( dialect.supportsWithClause() );
	}
}
