package com.xugu.dialect;

import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguIntervalTypeSupport;
import com.xugu.dialect.type.XuguIntervalTypeSupport.Subtype;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Offline unit tests: INTERVAL type hooks (A-TYP-014).
 *
 * <p><b>Known-limit:</b> Hibernate 7.4 ORM exposes {@code DURATION} + {@code INTERVAL_SECOND} only;
 * XuGu documents 13 subtypes ({@code reference/sql/datatype/datetime.md}). Subtype DDL is locked
 * in {@link XuguIntervalTypeSupport}; live acceptance uses native SQL round-trip IT.
 */
class XuguIntervalTypeTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void intervalTypeHooksWired_A_TYP_014() {
		assertEquals( XuguIntervalTypeSupport.DURATION_DDL, dialect.columnType( SqlTypes.DURATION ) );
		assertEquals( XuguIntervalTypeSupport.INTERVAL_SECOND_DDL, dialect.columnType( SqlTypes.INTERVAL_SECOND ) );
	}

	@Test
	void allDocumentedSubtypesLocked_A_TYP_014() {
		assertEquals( 13, XuguIntervalTypeSupport.documentedSubtypes().length );
		assertEquals( "interval year", XuguIntervalTypeSupport.ddlFor( Subtype.YEAR ) );
		assertEquals( "interval month", XuguIntervalTypeSupport.ddlFor( Subtype.MONTH ) );
		assertEquals( "interval day", XuguIntervalTypeSupport.ddlFor( Subtype.DAY ) );
		assertEquals( "interval hour", XuguIntervalTypeSupport.ddlFor( Subtype.HOUR ) );
		assertEquals( "interval minute", XuguIntervalTypeSupport.ddlFor( Subtype.MINUTE ) );
		assertEquals( "interval second", XuguIntervalTypeSupport.ddlFor( Subtype.SECOND ) );
		assertEquals( "interval year to month", XuguIntervalTypeSupport.ddlFor( Subtype.YEAR_TO_MONTH ) );
		assertEquals( "interval day to hour", XuguIntervalTypeSupport.ddlFor( Subtype.DAY_TO_HOUR ) );
		assertEquals( "interval day to minute", XuguIntervalTypeSupport.ddlFor( Subtype.DAY_TO_MINUTE ) );
		assertEquals( "interval day to second", XuguIntervalTypeSupport.ddlFor( Subtype.DAY_TO_SECOND ) );
		assertEquals( "interval hour to minute", XuguIntervalTypeSupport.ddlFor( Subtype.HOUR_TO_MINUTE ) );
		assertEquals( "interval hour to second", XuguIntervalTypeSupport.ddlFor( Subtype.HOUR_TO_SECOND ) );
		assertEquals( "interval minute to second", XuguIntervalTypeSupport.ddlFor( Subtype.MINUTE_TO_SECOND ) );
	}
}
