package com.xugu.dialect;

import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguXmlTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Offline unit tests: XML type hooks (A-TYP-016).
 *
 * <p><b>Known-limit:</b> DDL wired for {@code SqlTypes.SQLXML}; live acceptance uses native
 * SQL round-trip IT — not full ORM {@code @JdbcTypeCode(SQLXML)} entity mapping.
 */
class XuguXmlTypeTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void xmlTypeHooksWired_A_TYP_016() {
		assertEquals( XuguXmlTypeSupport.XML_DDL, dialect.columnType( SqlTypes.SQLXML ) );
	}

	@Test
	void documentedXmlConstantsLocked_A_TYP_016() {
		assertEquals( "xml", XuguXmlTypeSupport.XML_DDL );
		assertEquals( "xmltype", XuguXmlTypeSupport.XMLTYPE_DDL );
		assertEquals( 2L * 1024 * 1024 * 1024, XuguXmlTypeSupport.MAX_XML_LENGTH_BYTES );
	}
}
