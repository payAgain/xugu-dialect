package com.xugu.dialect;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.temptable.StandardTemporaryTableExporter;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.pagination.XuguLimitHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * I-005 / P-003 negative-only regression baseline for matrix rows marked
 * {@code 文档不允许} or {@code 延后} in {@code contracts/production-regression-baseline.md}.
 *
 * <p>Forms: dialect flag=false, SQL must not contain forbidden keywords, or
 * {@link Disabled} + SSOT entry for deferred rows (no invented SQL).
 */
class XuguNegativeRegressionBaselineTest {

	private static final String SSOT = "contracts/production-regression-baseline.md";

	private final XuguDialect dialect = new XuguDialect();
	private final XuguLimitHandler limits = XuguLimitHandler.INSTANCE;

	// -------------------------------------------------------------------------
	// Must-add (P-003 call-out)
	// -------------------------------------------------------------------------

	@Test
	void readUncommittedNotClaimed_A_XCUT_006() {
		assertEquals( Dialect.class, XuguDialect.class.getSuperclass(),
				"XuguDialect must extend Hibernate Dialect directly — no RU shortcut via MySQL/Oracle" );
		assertFalse( dialect.doesReadCommittedCauseWritersToBlockReaders() );
		assertFalse( dialect.doesRepeatableReadCauseReadersToBlockWriters() );
		// XuGu ISO_LEVEL: 0=READ ONLY, 1=RC, 2=RR, 3=SERIALIZABLE — no READ UNCOMMITTED level.
		assertFalse( declaresReadUncommittedSupport( dialect.getClass() ),
				"dialect must not declare READ UNCOMMITTED support (A-XCUT-006)" );
	}

	@Test
	void charterNoMySqlOracleInheritance_A_XCUT_010() {
		assertEquals( Dialect.class, XuguDialect.class.getSuperclass() );
		assertFalse( MySQLDialect.class.isAssignableFrom( XuguDialect.class ),
				"FORBIDDEN: extend MySQLDialect (A-XCUT-010 / ADR-0001)" );
		assertFalse( OracleDialect.class.isAssignableFrom( XuguDialect.class ),
				"FORBIDDEN: extend OracleDialect (A-XCUT-010 / ADR-0001)" );
	}

	@Test
	void charterNoSiblingDialectPort_A_XCUT_011() {
		assertTrue( XuguDialect.class.getName().startsWith( "com.xugu.dialect." ),
				"implementation must live in com.xugu.dialect — no sibling hibernate-dialect port (A-XCUT-011)" );
		assertEquals( "com.xugu.dialect.XuguDialect", XuguDialect.class.getName() );
	}

	// -------------------------------------------------------------------------
	// P-003 consolidate bundle (文档不允许 — evidence existed, now centralized)
	// -------------------------------------------------------------------------

	@Test
	void ansiFetchFirstNotEmitted_A_PAG_005() {
		Limit limit = new Limit( null, 10 );
		String sql = limits.processSql( "select id from t order by id", limit );
		assertTrue( sql.toLowerCase().contains( "limit ?" ) );
		assertFalse( sql.toLowerCase().contains( "fetch first" ),
				"must not emit ANSI FETCH FIRST (A-PAG-005)" );
		assertFalse( sql.toLowerCase().contains( "rows only" ),
				"must not emit ROWS ONLY pagination (A-PAG-005)" );
	}

	@Test
	void skipLockedNotSupported_A_LCK_004_C_SKIP_001() {
		assertFalse( dialect.supportsSkipLocked(), "A-LCK-004 / C-SKIP-001" );
		assertFalse( dialect.getForUpdateSkipLockedString().toLowerCase().contains( "skip locked" ) );
		assertFalse( dialect.getForUpdateSkipLockedString( "t_.id" ).toLowerCase().contains( "skip locked" ) );
		assertEquals( " for update", dialect.getWriteLockString( org.hibernate.Timeouts.SKIP_LOCKED ) );
	}

	@Test
	void forShareNotSupported_A_LCK_005() {
		String read = dialect.getReadLockString( org.hibernate.Timeouts.WAIT_FOREVER );
		assertFalse( read.toLowerCase().contains( "share" ), "FOR SHARE is 文档不允许 (A-LCK-005)" );
		assertTrue( read.toLowerCase().contains( "for update" ),
				"PESSIMISTIC_READ shim maps to FOR UPDATE, not share lock" );
	}

	@Test
	void tempTableFkNotEmitted_A_SCH_007() {
		assertInstanceOf( StandardTemporaryTableExporter.class, dialect.getTemporaryTableExporter() );
		String create = dialect.getTemporaryTableCreateCommand() + " HIB_NEG_TMP (id integer) "
				+ dialect.getTemporaryTableCreateOptions();
		assertFalse( create.toLowerCase().contains( "foreign key" ) );
		assertFalse( create.toLowerCase().contains( "references" ) );
	}

	@Test
	void enumDdlNotEmitted_C_DDL_004() {
		assertNull( dialect.getEnumTypeDeclaration( "Status", new String[] { "A", "B" } ),
				"native ENUM DDL is 文档不允许 (C-DDL-004)" );
	}

	@Test
	void definitionAIfNotExistsDeferred_A_DDL_007() {
		// Matrix A-DDL-007 remains 延后 at Definition A scope; I-003 C-DDL-001 covers IF NOT EXISTS wiring.
		assertTrue( dialect.supportsIfExistsBeforeTableName(),
				"C-DDL-001 anchor — row A-DDL-007 stays deferred in SSOT, not promoted to Definition A 可实现" );
		assertEquals( "create table if not exists", dialect.getCreateTableString() );
	}

	@Test
	void catalogsNotSupported_negativeOnly_A_SCH_003() {
		assertEquals( NameQualifierSupport.SCHEMA, dialect.getNameQualifierSupport() );
		assertFalse( dialect.getNameQualifierSupport().supportsCatalogs(),
				"catalog qualifier deferred (A-SCH-003)" );
	}

	@Test
	void p003NegativeOnlyChecklist_coversDocForbiddenBundle() {
		ansiFetchFirstNotEmitted_A_PAG_005();
		skipLockedNotSupported_A_LCK_004_C_SKIP_001();
		forShareNotSupported_A_LCK_005();
		tempTableFkNotEmitted_A_SCH_007();
		enumDdlNotEmitted_C_DDL_004();
		readUncommittedNotClaimed_A_XCUT_006();
		charterNoMySqlOracleInheritance_A_XCUT_010();
		charterNoSiblingDialectPort_A_XCUT_011();
	}

	// -------------------------------------------------------------------------
	// 延后 rows — @Disabled SSOT anchors (no positive SQL invention)
	// -------------------------------------------------------------------------

	@Disabled( "SSOT " + SSOT + " — A-TYP-014 INTERVAL deferred; revisit on app demand" )
	@Test
	void deferred_A_TYP_014_interval() {
	}

	@Disabled( "SSOT " + SSOT + " — A-TYP-016 XML deferred" )
	@Test
	void deferred_A_TYP_016_xml() {
	}

	@Disabled( "SSOT " + SSOT + " — A-TYP-017 geometric/spatial deferred" )
	@Test
	void deferred_A_TYP_017_spatial() {
	}

	@Disabled( "SSOT " + SSOT + " — A-TYP-018 UDT deferred" )
	@Test
	void deferred_A_TYP_018_udt() {
	}

	@Disabled( "SSOT " + SSOT + " — A-DDL-008 table partitioning deferred" )
	@Test
	void deferred_A_DDL_008_partitioning() {
	}

	@Disabled( "SSOT " + SSOT + " — A-DDL-009 column/table ENCRYPT deferred" )
	@Test
	void deferred_A_DDL_009_encrypt() {
	}

	@Disabled( "SSOT " + SSOT + " — A-PAG-004 TOP syntax deferred; LIMIT preferred" )
	@Test
	void deferred_A_PAG_004_top() {
	}

	@Disabled( "SSOT " + SSOT + " — A-PAG-006 ROWNUM pagination deferred" )
	@Test
	void deferred_A_PAG_006_rownum() {
	}

	@Disabled( "SSOT " + SSOT + " — A-LCK-006 LOCK TABLE deferred" )
	@Test
	void deferred_A_LCK_006_lockTable() {
	}

	@Disabled( "SSOT " + SSOT + " — A-IDN-005 identity_mode session params deferred" )
	@Test
	void deferred_A_IDN_005_identityMode() {
	}

	@Disabled( "SSOT " + SSOT + " — A-FUN-019 regexp_* HQL deferred" )
	@Test
	void deferred_A_FUN_019_regexp() {
	}

	@Disabled( "SSOT " + SSOT + " — A-FUN-020 geometric functions deferred (pairs A-TYP-017)" )
	@Test
	void deferred_A_FUN_020_geometric() {
	}

	@Disabled( "SSOT " + SSOT + " — A-FUN-021 XML functions deferred (pairs A-TYP-016)" )
	@Test
	void deferred_A_FUN_021_xmlFunctions() {
	}

	@Disabled( "SSOT " + SSOT + " — A-SCH-017 advanced index types deferred" )
	@Test
	void deferred_A_SCH_017_advancedIndexes() {
	}

	@Disabled( "SSOT " + SSOT + " — A-XCUT-012 Maven Central publish deferred to Ship" )
	@Test
	void deferred_A_XCUT_012_mavenCentralPublish() {
	}

	@Disabled( "SSOT " + SSOT + " — C-JSON-006 json_table deferred pending doc confirm" )
	@Test
	void deferred_C_JSON_006_jsonTable() {
	}

	@Disabled( "SSOT " + SSOT + " — C-SRV-001 server configuration probe deferred" )
	@Test
	void deferred_C_SRV_001_serverConfiguration() {
	}

	@Disabled( "SSOT " + SSOT + " — C-SEL-001 DialectSelector deferred; A-SPI-* resolver delivered" )
	@Test
	void deferred_C_SEL_001_dialectSelector() {
	}

	private static boolean declaresReadUncommittedSupport(Class<?> dialectClass) {
		return Stream.concat(
				Arrays.stream( dialectClass.getDeclaredMethods() ),
				Arrays.stream( dialectClass.getMethods() ) )
				.map( Method::getName )
				.anyMatch( name -> name.toLowerCase().contains( "readuncommitted" )
						|| name.toLowerCase().contains( "read_uncommitted" ) );
	}
}
