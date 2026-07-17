package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.temptable.XuguGlobalTemporaryTableStrategy;
import com.xugu.dialect.temptable.XuguLocalTemporaryTableStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated live-DB IT for P-007: schema, local temp, comments, FK, unique, truncate, index.
 * All objects use {@code HIB_P007_} prefix; cleanup checklist runs in finally.
 */
class XuguSchemaTempCommentIT {

	private static final String SCHEMA = "HIB_P007_SCH";
	private static final String PARENT = "HIB_P007_PARENT";
	private static final String CHILD = "HIB_P007_CHILD";
	private static final String COMMENT_TBL = "HIB_P007_CMT";
	private static final String UNIQUE_TBL = "HIB_P007_UK";
	private static final String TRUNC_TBL = "HIB_P007_TR";
	private static final String IDX_TBL = "HIB_P007_IDX";
	private static final String IDX_NAME = "HIB_P007_I1";
	private static final String LOCAL_TEMP = "HIB_P007_LTMP";
	private static final String GLOBAL_TEMP = "HIB_P007_GTMP";
	private static final String QUAL_TBL = "HIB_P007_QT";

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void schemaTempCommentFkTruncate_A_SCH() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		List<String> cleanupLog = new ArrayList<>();
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			cleanupAll( st, cleanupLog );

			// --- A-SCH-001 schema create/drop ---
			String createSch = dialect.getCreateSchemaCommand( SCHEMA )[0];
			st.execute( createSch );
			assertTrue( schemaExists( st, SCHEMA ), "schema should exist after CREATE SCHEMA" );

			// --- A-SCH-002 qualified name schema.table ---
			st.execute( "CREATE TABLE " + SCHEMA + "." + QUAL_TBL + " (id INT PRIMARY KEY)" );
			st.execute( "INSERT INTO " + SCHEMA + "." + QUAL_TBL + " VALUES (1)" );
			try ( ResultSet rs = st.executeQuery( "SELECT id FROM " + SCHEMA + "." + QUAL_TBL ) ) {
				assertTrue( rs.next() );
				assertEquals( 1, rs.getInt( 1 ) );
			}

			// --- A-SCH-004 / A-SCH-006 local temp + ON COMMIT ---
			String localCreate = dialect.getLocalTemporaryTableStrategy().getTemporaryTableCreateCommand()
					+ " " + LOCAL_TEMP + " (id INT, name VARCHAR(32)) "
					+ dialect.getLocalTemporaryTableStrategy().getTemporaryTableCreateOptions();
			assertTrue( localCreate.startsWith( XuguLocalTemporaryTableStrategy.CREATE_COMMAND ) );
			assertTrue( localCreate.contains( XuguLocalTemporaryTableStrategy.CREATE_OPTIONS ) );
			assertFalse( localCreate.toLowerCase().contains( "foreign key" ), "A-SCH-007: no FK on temp" );
			st.execute( localCreate );
			st.execute( "INSERT INTO " + LOCAL_TEMP + " VALUES (1, 't')" );
			try ( ResultSet rs = st.executeQuery( "SELECT name FROM " + LOCAL_TEMP + " WHERE id = 1" ) ) {
				assertTrue( rs.next() );
				assertEquals( "t", rs.getString( 1 ) );
			}
			st.execute( dialect.getLocalTemporaryTableStrategy().getTemporaryTableDropCommand()
					+ " " + LOCAL_TEMP );
			cleanupLog.add( "dropped local temp " + LOCAL_TEMP );

			// --- A-SCH-005 global temp: only if support_global_tab ON ---
			boolean globalOn = isSupportGlobalTabOn( st );
			if ( globalOn ) {
				String globalCreate = dialect.getGlobalTemporaryTableStrategy().getTemporaryTableCreateCommand()
						+ " " + GLOBAL_TEMP + " (id INT) "
						+ dialect.getGlobalTemporaryTableStrategy().getTemporaryTableCreateOptions();
				assertTrue( globalCreate.startsWith( XuguGlobalTemporaryTableStrategy.CREATE_COMMAND ) );
				st.execute( globalCreate );
				st.execute( "INSERT INTO " + GLOBAL_TEMP + " VALUES (7)" );
				st.execute( "DROP TABLE " + GLOBAL_TEMP );
				cleanupLog.add( "dropped global temp " + GLOBAL_TEMP );
			}
			else {
				cleanupLog.add( "SKIPPED global temp IT: support_global_tab is OFF (DDL strings still unit-tested)" );
			}

			// --- A-SCH-008 / A-SCH-009 COMMENT ON; A-SCH-010 inline alternate ---
			st.execute( "CREATE TABLE " + COMMENT_TBL + " (c1 INT" + XuguDialect.inlineColumnComment( "col-c1" )
					+ ")" + XuguDialect.inlineTableComment( "tbl-inline" ) );
			st.execute( XuguDialect.commentOnTableSql( COMMENT_TBL, "tbl-on" ) );
			st.execute( XuguDialect.commentOnColumnSql( COMMENT_TBL + ".c1", "col-on" ) );

			// --- A-SCH-011 UNIQUE + A-SCH-012 FK between permanent tables ---
			st.execute( "CREATE TABLE " + PARENT + " (id INT PRIMARY KEY, code VARCHAR(32))" );
			st.execute( "CREATE TABLE " + CHILD + " (id INT PRIMARY KEY, parent_id INT)" );
			String addFk = "ALTER TABLE " + CHILD
					+ dialect.getAddForeignKeyConstraintString(
							"HIB_P007_FK",
							new String[] { "parent_id" },
							PARENT,
							new String[] { "id" },
							false );
			st.execute( addFk );
			st.execute( "INSERT INTO " + PARENT + " VALUES (1, 'A')" );
			st.execute( "INSERT INTO " + CHILD + " VALUES (10, 1)" );
			boolean fkRejected = false;
			try {
				st.execute( "INSERT INTO " + CHILD + " VALUES (11, 999)" );
			}
			catch ( SQLException e ) {
				fkRejected = true;
			}
			assertTrue( fkRejected, "FK should reject orphan child row" );

			st.execute( "CREATE TABLE " + UNIQUE_TBL + " (id INT PRIMARY KEY, email VARCHAR(64))" );
			st.execute( "ALTER TABLE " + UNIQUE_TBL + " ADD CONSTRAINT HIB_P007_UQ UNIQUE (email)" );
			st.execute( "INSERT INTO " + UNIQUE_TBL + " VALUES (1, 'a@x')" );
			boolean ukRejected = false;
			try {
				st.execute( "INSERT INTO " + UNIQUE_TBL + " VALUES (2, 'a@x')" );
			}
			catch ( SQLException e ) {
				ukRejected = true;
			}
			assertTrue( ukRejected, "UNIQUE should reject duplicate email" );

			// --- A-SCH-013 CHECK ---
			st.execute( "ALTER TABLE " + UNIQUE_TBL + " ADD CONSTRAINT HIB_P007_CK CHECK (id > 0)" );

			// --- A-SCH-014 DROP CONSTRAINT (live) ---
			st.execute( "ALTER TABLE " + UNIQUE_TBL + " " + dialect.getDropForeignKeyString() + " HIB_P007_CK" );
			boolean checkStillEnforced = false;
			try {
				st.execute( "INSERT INTO " + UNIQUE_TBL + " VALUES (-1, 'neg@x')" );
			}
			catch ( SQLException e ) {
				checkStillEnforced = true;
			}
			assertFalse( checkStillEnforced, "CHECK constraint should be dropped" );
			st.execute( "DELETE FROM " + UNIQUE_TBL + " WHERE id = -1" );

			// --- A-SCH-015 TRUNCATE ---
			st.execute( "CREATE TABLE " + TRUNC_TBL + " (id INT PRIMARY KEY)" );
			st.execute( "INSERT INTO " + TRUNC_TBL + " VALUES (1)" );
			st.execute( dialect.getTruncateTableStatement( TRUNC_TBL ) );
			try ( ResultSet rs = st.executeQuery( "SELECT COUNT(*) FROM " + TRUNC_TBL ) ) {
				assertTrue( rs.next() );
				assertEquals( 0, rs.getInt( 1 ) );
			}

			// --- A-SCH-016 CREATE INDEX ---
			st.execute( "CREATE TABLE " + IDX_TBL + " (id INT PRIMARY KEY, name VARCHAR(32))" );
			st.execute( dialect.getCreateIndexString( false ) + " " + IDX_NAME + " ON " + IDX_TBL + " (name)" );

			cleanupLog.add( "IT body completed OK" );
		}
		catch ( Exception e ) {
			fail( "P-007 IT failed: " + e.getMessage(), e );
		}
		finally {
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				cleanupAll( st, cleanupLog );
			}
			catch ( Exception e ) {
				cleanupLog.add( "FINAL CLEANUP ERROR: " + e.getMessage() );
			}
			// Always print cleanup checklist for evidence
			System.out.println( "=== HIB_P007 CLEANUP CHECKLIST ===" );
			for ( String line : cleanupLog ) {
				System.out.println( line );
			}
		}
	}

	private static void cleanupAll(Statement st, List<String> log) {
		// Order: child FK first, then parents, temps, schema objects, schema last
		execIgnore( st, "DROP TABLE IF EXISTS " + CHILD, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + PARENT, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + COMMENT_TBL, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + UNIQUE_TBL, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + TRUNC_TBL, log );
		execIgnore( st, "DROP INDEX IF EXISTS " + IDX_TBL + "." + IDX_NAME, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + IDX_TBL, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + LOCAL_TEMP, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + GLOBAL_TEMP, log );
		execIgnore( st, "DROP TABLE IF EXISTS " + SCHEMA + "." + QUAL_TBL, log );
		// schema.md: DROP SCHEMA name [RESTRICT|CASCADE] — no IF EXISTS
		execIgnore( st, "DROP SCHEMA " + SCHEMA + " CASCADE", log );
		execIgnore( st, "DROP SCHEMA " + SCHEMA, log );
	}

	private static void execIgnore(Statement st, String sql, List<String> log) {
		try {
			st.execute( sql );
			log.add( "OK: " + sql );
		}
		catch ( Exception e ) {
			log.add( "skip: " + sql + " (" + e.getMessage() + ")" );
		}
	}

	private static boolean schemaExists(Statement st, String schema) throws SQLException {
		try ( ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) FROM USER_SCHEMAS WHERE SCHEMA_NAME = '" + schema.toUpperCase() + "'" ) ) {
			if ( rs.next() && rs.getInt( 1 ) > 0 ) {
				return true;
			}
		}
		catch ( SQLException ignored ) {
			// try DBA / ALL views
		}
		try ( ResultSet rs = st.executeQuery(
				"SELECT COUNT(*) FROM ALL_SCHEMAS WHERE SCHEMA_NAME = '" + schema.toUpperCase() + "'" ) ) {
			return rs.next() && rs.getInt( 1 ) > 0;
		}
	}

	private static boolean isSupportGlobalTabOn(Statement st) {
		try ( ResultSet rs = st.executeQuery( "SHOW support_global_tab" ) ) {
			if ( rs.next() ) {
				String v = rs.getString( 1 );
				if ( v == null && rs.getMetaData().getColumnCount() >= 2 ) {
					v = rs.getString( 2 );
				}
				if ( v == null ) {
					return false;
				}
				String n = v.trim().toLowerCase();
				return "on".equals( n ) || "true".equals( n ) || "1".equals( n ) || "t".equals( n );
			}
		}
		catch ( Exception ignored ) {
			// fallback probe
		}
		try {
			st.execute( "CREATE GLOBAL TEMP TABLE HIB_P007_PROBE (id INT)" );
			st.execute( "DROP TABLE HIB_P007_PROBE" );
			return true;
		}
		catch ( Exception e ) {
			return false;
		}
	}
}
