package com.xugu.dialect;

import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.temptable.XuguLocalTemporaryTableStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for C-BULK-001…003 bulk mutation fallback wiring.
 */
class XuguBulkMutationSupportTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void supportsSubqueryOnMutatingTableIsFalse_C_BULK_003() {
		assertFalse( dialect.supportsSubqueryOnMutatingTable() );
	}

	@Test
	void localTemporaryTableStrategyForBulkMutation_C_BULK_001() {
		assertEquals( TemporaryTableKind.LOCAL, dialect.getSupportedTemporaryTableKind() );
		assertSame( XuguLocalTemporaryTableStrategy.INSTANCE, dialect.getLocalTemporaryTableStrategy() );
		assertTrue(
				dialect.getTemporaryTableCreateCommand().toLowerCase().contains( "local temporary table" ),
				dialect.getTemporaryTableCreateCommand() );
		assertEquals(
				XuguLocalTemporaryTableStrategy.CREATE_COMMAND,
				dialect.getTemporaryTableCreateCommand() );
	}
}
