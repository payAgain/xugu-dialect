package com.xugu.dialect;

import java.time.Duration;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguIntervalJdbcType;
import com.xugu.dialect.type.XuguIntervalTypeSupport;
import com.xugu.dialect.type.XuguIntervalTypeSupport.Subtype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests: INTERVAL type hooks (A-TYP-014).
 *
 * <p>Hibernate 7.4 ORM exposes {@code DURATION} + {@code INTERVAL_SECOND} only;
 * XuGu documents 13 subtypes ({@code reference/sql/datatype/datetime.md}). Subtype DDL is locked
 * in {@link XuguIntervalTypeSupport}; entity ORM uses {@link XuguIntervalJdbcType}.
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

	@Test
	void intervalJdbcTypesContributed_A_TYP_014() {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.build();
		SessionFactoryImplementor sf = null;
		try {
			sf = (SessionFactoryImplementor) new MetadataSources( registry )
					.buildMetadata()
					.buildSessionFactory();
			var jdbcTypes = sf.getTypeConfiguration().getJdbcTypeRegistry();
			JdbcType duration = jdbcTypes.getDescriptor( SqlTypes.DURATION );
			JdbcType intervalSecond = jdbcTypes.getDescriptor( SqlTypes.INTERVAL_SECOND );
			assertInstanceOf( XuguIntervalJdbcType.class, duration );
			assertInstanceOf( XuguIntervalJdbcType.class, intervalSecond );
			assertEquals( SqlTypes.DURATION, duration.getDefaultSqlTypeCode() );
			assertEquals( SqlTypes.INTERVAL_SECOND, intervalSecond.getDefaultSqlTypeCode() );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Test
	void intervalJdbcTypeFormatParseRoundTrip_A_TYP_014() {
		Duration dayToSecond = Duration.ofDays( 3 ).plusHours( 12 ).plusMinutes( 48 ).plusSeconds( 56 );
		Duration seconds = Duration.ofSeconds( 45, 550_000_000L );

		String dtsLiteral = XuguIntervalJdbcType.DURATION.format( dayToSecond );
		assertEquals( "3 12:48:56", dtsLiteral );
		assertTrue( XuguIntervalJdbcType.approximatelyEqual(
				dayToSecond, XuguIntervalJdbcType.DURATION.parse( dtsLiteral ) ) );

		String secLiteral = XuguIntervalJdbcType.INTERVAL_SECOND.format( seconds );
		assertEquals( "45.55", secLiteral );
		assertTrue( XuguIntervalJdbcType.approximatelyEqual(
				seconds, XuguIntervalJdbcType.INTERVAL_SECOND.parse( secLiteral ) ) );

		// DEF_INTERVAL_STYLE variants commonly seen on read
		assertTrue( XuguIntervalJdbcType.approximatelyEqual(
				seconds, XuguIntervalJdbcType.INTERVAL_SECOND.parse( "0:00:45.55" ) ) );
		assertTrue( XuguIntervalJdbcType.approximatelyEqual(
				dayToSecond, XuguIntervalJdbcType.DURATION.parse( "P3DT12H48M56S" ) ) );
	}
}
