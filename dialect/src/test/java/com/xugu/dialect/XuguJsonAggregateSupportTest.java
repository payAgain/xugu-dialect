package com.xugu.dialect;

import org.hibernate.type.spi.TypeConfiguration;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.aggregate.XuguAggregateSupport;
import com.xugu.dialect.function.XuguJsonArrayAggFunction;
import com.xugu.dialect.function.XuguJsonObjectAggFunction;
import com.xugu.dialect.type.XuguCastingJsonJdbcType;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class XuguJsonAggregateSupportTest {

	@Test
	void dialectWiresAggregateSupportAndCastingJsonType() {
		XuguDialect dialect = new XuguDialect();
		assertSame( XuguAggregateSupport.INSTANCE, dialect.getAggregateSupport() );
		assertNotNull( XuguCastingJsonJdbcType.INSTANCE );
	}

	@Test
	void jsonAggFunctionsConstruct() {
		TypeConfiguration types = new TypeConfiguration();
		assertInstanceOf( XuguJsonArrayAggFunction.class, new XuguJsonArrayAggFunction( types ) );
		assertInstanceOf( XuguJsonObjectAggFunction.class, new XuguJsonObjectAggFunction( types ) );
	}
}
