package com.xugu.dialect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke test: dialect class loads and instantiates without a live database.
 */
class XuguDialectTest {

	@Test
	void dialectInstantiates() {
		XuguDialect dialect = new XuguDialect();
		assertNotNull(dialect);
		assertNotNull(dialect.getVersion());
	}
}
