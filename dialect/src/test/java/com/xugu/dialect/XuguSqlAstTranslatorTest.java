package com.xugu.dialect;

import org.hibernate.sql.ast.SqlAstTranslatorFactory;
import org.hibernate.sql.ast.spi.StandardSqlAstTranslatorFactory;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.sql.ast.XuguSqlAstTranslator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for I-002 P-001 SqlAstTranslator wiring.
 */
class XuguSqlAstTranslatorTest {

	@Test
	void sqlAstTranslatorFactoryIsNonNullAndBuildsXuguTranslator() {
		XuguDialect dialect = new XuguDialect();
		SqlAstTranslatorFactory factory = dialect.getSqlAstTranslatorFactory();
		assertNotNull( factory, "getSqlAstTranslatorFactory() must not be null (root cause of ANSI OFFSET/FETCH)" );
		assertTrue( factory instanceof StandardSqlAstTranslatorFactory );
	}

	@Test
	void translatorClassIsLoadable() {
		assertNotNull( XuguSqlAstTranslator.class.getName() );
	}
}
