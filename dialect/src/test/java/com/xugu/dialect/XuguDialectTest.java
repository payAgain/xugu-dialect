package com.xugu.dialect;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.query.sqm.CastType;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for XuguDialect type/DDL/identifier hooks (no live DB).
 */
class XuguDialectTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void dialectInstantiates() {
		assertNotNull( dialect.getVersion() );
		assertEquals( 12, dialect.getVersion().getMajor() );
	}

	@Test
	void columnTypesMatchXuguDocs() {
		assertEquals( "tinyint", expose( SqlTypes.TINYINT ) );
		assertEquals( "smallint", expose( SqlTypes.SMALLINT ) );
		assertEquals( "integer", expose( SqlTypes.INTEGER ) );
		assertEquals( "bigint", expose( SqlTypes.BIGINT ) );
		assertEquals( "numeric($p,$s)", expose( SqlTypes.NUMERIC ) );
		assertEquals( "numeric($p,$s)", expose( SqlTypes.DECIMAL ) );
		assertEquals( "float", expose( SqlTypes.FLOAT ) );
		assertEquals( "float", expose( SqlTypes.REAL ) );
		assertEquals( "double", expose( SqlTypes.DOUBLE ) );
		assertEquals( "char($l)", expose( SqlTypes.CHAR ) );
		assertEquals( "varchar($l)", expose( SqlTypes.VARCHAR ) );
		assertEquals( "char($l)", expose( SqlTypes.NCHAR ) );
		assertEquals( "varchar($l)", expose( SqlTypes.NVARCHAR ) );
		assertEquals( "boolean", expose( SqlTypes.BOOLEAN ) );
		assertEquals( "date", expose( SqlTypes.DATE ) );
		assertEquals( "time($p)", expose( SqlTypes.TIME ) );
		assertEquals( "timestamp($p)", expose( SqlTypes.TIMESTAMP ) );
		assertEquals( "timestamp($p)", expose( SqlTypes.TIMESTAMP_UTC ) );
		assertEquals( "timestamp($p) with time zone", expose( SqlTypes.TIMESTAMP_WITH_TIMEZONE ) );
		assertEquals( "binary", expose( SqlTypes.BINARY ) );
		assertEquals( "binary", expose( SqlTypes.VARBINARY ) );
		assertEquals( "blob", expose( SqlTypes.LONGVARBINARY ) );
		assertEquals( "blob", expose( SqlTypes.LONG32VARBINARY ) );
		assertEquals( "blob", expose( SqlTypes.BLOB ) );
		assertEquals( "clob", expose( SqlTypes.CLOB ) );
		assertEquals( "clob", expose( SqlTypes.NCLOB ) );
		assertEquals( "guid", expose( SqlTypes.UUID ) );
		assertEquals( "json", expose( SqlTypes.JSON ) );
		assertEquals( "interval day to second", expose( SqlTypes.DURATION ) );
		assertEquals( "interval second", expose( SqlTypes.INTERVAL_SECOND ) );
	}

	@Test
	void sizeAndPrecisionDefaults() {
		assertEquals( 60_000, dialect.getMaxVarcharLength() );
		assertEquals( 65_536, dialect.getMaxVarbinaryLength() );
		assertEquals( 12, dialect.getDefaultDecimalPrecision() );
		assertEquals( 3, dialect.getDefaultTimestampPrecision() );
		assertEquals( SqlTypes.BOOLEAN, dialect.getPreferredSqlTypeCodeForBoolean() );
		assertTrue( dialect.stripsTrailingSpacesFromChar() );
	}

	@Test
	void quoteCharsAreDoubleQuote() {
		assertEquals( '"', dialect.openQuote() );
		assertEquals( '"', dialect.closeQuote() );
		assertEquals( "\"Order\"", dialect.toQuotedIdentifier( "Order" ) );
	}

	@Test
	void ddlHelpersMatchXuguSyntax() {
		assertEquals( "create table if not exists", dialect.getCreateTableString() );
		assertEquals( "add column", dialect.getAddColumnString() );
		assertEquals( "alter table T", dialect.getAlterTableString( "T" ) );
		assertEquals( "drop table T", dialect.getDropTableString( "T" ) );
		assertEquals( "", dialect.getNullColumnString() );
	}

	@Test
	void booleanLiteralsAreTrueFalse() {
		assertEquals( "true", dialect.toBooleanValueString( true ) );
		assertEquals( "false", dialect.toBooleanValueString( false ) );
	}

	@Test
	void keywordsIncludeTcl() {
		assertTrue( dialect.getKeywords().contains( "begin" ) );
		assertTrue( dialect.getKeywords().contains( "commit" ) );
		assertTrue( dialect.getKeywords().contains( "rollback" ) );
	}

	@Test
	void castPatternDefaultUsesStandardCastSyntax_A_TYP_019() {
		String pattern = dialect.castPattern( CastType.STRING, CastType.INTEGER );
		assertNotNull( pattern );
		String lower = pattern.toLowerCase();
		assertTrue( lower.contains( "cast" ), "castPattern should use CAST syntax: " + pattern );
		assertTrue( pattern.contains( "?1" ), "castPattern should bind expression: " + pattern );
		assertTrue( pattern.contains( "?2" ), "castPattern should bind target type: " + pattern );
	}

	@Test
	void unquotedIdentifiersFoldToUppercase_A_XCUT_001() throws Exception {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.build();
		try {
			JdbcEnvironment jdbcEnvironment = registry.getService( JdbcEnvironment.class );
			XuguDialect wiredDialect = (XuguDialect) jdbcEnvironment.getDialect();
			IdentifierHelper helper = wiredDialect.buildIdentifierHelper(
					org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder.from( jdbcEnvironment ),
					null );
			assertEquals(
					"MYTABLE",
					helper.toMetaDataObjectName( helper.toIdentifier( "mytable", false ) ) );
			assertEquals(
					"mixedCase",
					helper.toMetaDataObjectName( helper.toIdentifier( "mixedCase", true ) ) );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Test
	void isolationLevelHooksMatchXuguIsoLevel_A_XCUT_005() {
		assertFalse( dialect.doesReadCommittedCauseWritersToBlockReaders() );
		assertFalse( dialect.doesRepeatableReadCauseReadersToBlockWriters() );
	}

	/** Expose protected {@code columnType} for assertions. */
	private String expose(int sqlTypeCode) {
		return new XuguDialect() {
			String of(int code) {
				return columnType( code );
			}
		}.of( sqlTypeCode );
	}
}
