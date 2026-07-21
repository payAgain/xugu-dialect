package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.ddl.XuguIndexDdlSupport;
import com.xugu.dialect.metadata.XuguCatalogMetadataSupport;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-SCH-003 JDBC catalog metadata + A-SCH-017 functional/BITMAP indexes.
 */
class XuguCatalogAndIndexExtensionsIT {

	private static final String TABLE = "HIB_I009_P008_IDX";
	private static final String FUNC_IDX = "HIB_I009_P008_FUNC";
	private static final String BITMAP_IDX = "HIB_I009_P008_BM";

	@Test
	void jdbcCatalogAlignsWithCurrentDb_A_SCH_003() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			assertTrue(
					XuguCatalogMetadataSupport.jdbcCatalogAlignsWithCurrentDatabase( c ),
					"JDBC catalog should align with current_db() under NONE" );
			final String currentDb = XuguCatalogMetadataSupport.queryCurrentDatabase( c );
			assertNotNull( currentDb );
			assertFalseBlank( currentDb, "current_db() should be non-empty" );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "XuguDB unreachable with integration gate ON: " + e.getMessage(), e );
		}
	}

	@Test
	void functionalAndBitmapIndexNativeRoundTrip_A_SCH_017() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			cleanup( st );

			st.execute( "create table " + TABLE + " (id int, name varchar(100), birth date)" );
			st.execute( "insert into " + TABLE + " values (1, 'alpha', date '2000-01-01')" );
			st.execute( "insert into " + TABLE + " values (2, 'beta', date '1990-06-15')" );

			st.execute( XuguIndexDdlSupport.createFunctionalIndexSql( TABLE, FUNC_IDX, "len(name)" ) );
			st.execute( XuguIndexDdlSupport.createBitmapIndexSql( TABLE, BITMAP_IDX, "birth" ) );

			assertIndexExists( st, TABLE, FUNC_IDX );
			assertIndexExists( st, TABLE, BITMAP_IDX );

			try ( ResultSet rs = st.executeQuery(
					"select id from " + TABLE + " where len(name) = 5 order by id" ) ) {
				assertTrue( rs.next() );
				assertEquals( 1, rs.getInt( 1 ) );
			}
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Catalog/index IT failed: " + e.getMessage(), e );
		}
		finally {
			try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
				cleanup( st );
			}
			catch ( Exception ignored ) {
			}
		}
	}

	private static void cleanup(Statement st) throws Exception {
		try {
			st.execute( "drop index if exists " + TABLE + "." + FUNC_IDX );
		}
		catch ( Exception ignored ) {
		}
		try {
			st.execute( "drop index if exists " + TABLE + "." + BITMAP_IDX );
		}
		catch ( Exception ignored ) {
		}
		st.execute( "drop table if exists " + TABLE );
	}

	private static void assertIndexExists(Statement st, String table, String indexName) throws Exception {
		try ( ResultSet rs = st.executeQuery(
				"select uc.index_name from sys_indexes uc "
						+ "join sys_tables ut on uc.table_id = ut.table_id "
						+ "where ut.table_name = '" + table + "' "
						+ "and uc.index_name = '" + indexName + "'" ) ) {
			assertTrue( rs.next(), "index " + indexName + " on " + table + " expected" );
		}
	}

	private static void assertFalseBlank(String value, String message) {
		assertTrue( value != null && !value.isBlank(), message );
	}
}
