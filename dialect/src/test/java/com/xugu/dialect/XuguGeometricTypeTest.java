package com.xugu.dialect;

import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguGeometricTypeSupport;
import com.xugu.dialect.type.XuguGeometricTypeSupport.Kind;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Offline unit tests: geometric type hooks (A-TYP-017).
 *
 * <p><b>Known-limit:</b> POINT/GEOMETRY SqlTypes → {@code point} DDL; all seven documented
 * subtypes locked in {@link XuguGeometricTypeSupport}. Live acceptance uses native SQL
 * round-trip IT — not full ORM geometric entity mapping.
 */
class XuguGeometricTypeTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void pointTypeHooksWired_A_TYP_017() {
		assertEquals( XuguGeometricTypeSupport.POINT_DDL, dialect.columnType( SqlTypes.POINT ) );
		assertEquals( XuguGeometricTypeSupport.GEOMETRY_DDL, dialect.columnType( SqlTypes.GEOMETRY ) );
	}

	@Test
	void allDocumentedKindsLocked_A_TYP_017() {
		assertEquals( 7, XuguGeometricTypeSupport.documentedKinds().length );
		assertEquals( "point", Kind.POINT.ddl() );
		assertEquals( "line", Kind.LINE.ddl() );
		assertEquals( "lseg", Kind.LSEG.ddl() );
		assertEquals( "box", Kind.BOX.ddl() );
		assertEquals( "path", Kind.PATH.ddl() );
		assertEquals( "polygon", Kind.POLYGON.ddl() );
		assertEquals( "circle", Kind.CIRCLE.ddl() );
		assertEquals( "box", XuguGeometricTypeSupport.ddlFor( Kind.BOX ) );
	}
}
