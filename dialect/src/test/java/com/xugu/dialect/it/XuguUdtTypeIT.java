package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguUdtTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-018 UDT native SQL round-trip.
 *
 * <p>SQL shapes from {@code reference/sql/datatype/udt.md} — OBJECT constructor insert,
 * VARRAY table column, TABLE nested-table column. ORM entity UDT mapping remains known-limit.
 */
class XuguUdtTypeIT {

	private static final String OBJ_TYPE = "HIB_I009_P005_OBJ";
	private static final String OBJ_TABLE = "HIB_I009_P005_OBJ_TAB";
	private static final String VAR_TYPE = "HIB_I009_P005_VAR";
	private static final String VAR_TABLE = "HIB_I009_P005_VAR_TAB";
	private static final String TAB_TYPE = "HIB_I009_P005_TAB";
	private static final String TAB_TABLE = "HIB_I009_P005_TAB_TAB";

	@Test
	void udtNativeRoundTrip_A_TYP_018() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		assertEquals( 3, XuguUdtTypeSupport.documentedKinds().length, "A-TYP-018" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				dropAll( st );

				// udt.md §结构类型 — udt_obj_type + obj_tab example
				st.execute( XuguUdtTypeSupport.createObjectTypeSql(
						OBJ_TYPE,
						"n numeric, class varchar2, type varchar, dt date"
				) );
				st.execute( """
						CREATE TABLE %s (
						  id INT PRIMARY KEY,
						  state VARCHAR2,
						  udt_obj %s
						)
						""".formatted( OBJ_TABLE, OBJ_TYPE ) );
				st.execute( """
						INSERT INTO %s (id, state, udt_obj) VALUES (
						  1,
						  'constructor insert',
						  %s(1.0, 'layer1', '%s', '2025-06-14 00:00:00')
						)
						""".formatted( OBJ_TABLE, OBJ_TYPE, OBJ_TYPE ) );

				try ( ResultSet rs = st.executeQuery( "SELECT udt_obj FROM " + OBJ_TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next(), "expected OBJECT row" );
					String value = rs.getString( 1 );
					assertNotNull( value );
					assertTrue( value.contains( "1" ) && value.contains( "layer1" ),
							"OBJECT round-trip: " + value );
				}

				// udt.md §数组类型 — var_test + udt_varray_tab example
				st.execute( XuguUdtTypeSupport.createVarrayTypeSql( VAR_TYPE, 3, "varchar" ) );
				st.execute( """
						CREATE TABLE %s (
						  id INT PRIMARY KEY,
						  udt_varray_type %s
						)
						""".formatted( VAR_TABLE, VAR_TYPE ) );
				st.execute( """
						INSERT INTO %s VALUES (1, %s('chongqing', 'youyang', 'taohuayuan'))
						""".formatted( VAR_TABLE, VAR_TYPE ) );

				try ( ResultSet rs = st.executeQuery(
						"SELECT udt_varray_type FROM " + VAR_TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next(), "expected VARRAY row" );
					String value = rs.getString( 1 );
					assertNotNull( value );
					assertTrue( value.contains( "chongqing" ) && value.contains( "youyang" ),
							"VARRAY round-trip: " + value );
				}

				// udt.md §嵌套表类型 — udt_tab_type + tab_type example
				st.execute( XuguUdtTypeSupport.createTableTypeSql( TAB_TYPE, "bigint" ) );
				st.execute( """
						CREATE TABLE %s (
						  id INT PRIMARY KEY,
						  type_tab %s
						)
						""".formatted( TAB_TABLE, TAB_TYPE ) );
				st.execute( """
						INSERT INTO %s VALUES (1, %s(1, 2, 3, 4, 5))
						""".formatted( TAB_TABLE, TAB_TYPE ) );

				try ( ResultSet rs = st.executeQuery(
						"SELECT type_tab FROM " + TAB_TABLE + " WHERE id = 1" ) ) {
					assertTrue( rs.next(), "expected TABLE row" );
					String value = rs.getString( 1 );
					assertNotNull( value );
					assertTrue( value.contains( "1" ) && value.contains( "5" ),
							"TABLE round-trip: " + value );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					dropAll( st );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "UDT native IT failed: " + e.getMessage(), e );
		}
	}

	private static void dropAll(Statement st) {
		safeExecute( st, "DROP TABLE IF EXISTS " + OBJ_TABLE );
		safeExecute( st, "DROP TABLE IF EXISTS " + VAR_TABLE );
		safeExecute( st, "DROP TABLE IF EXISTS " + TAB_TABLE );
		safeExecute( st, XuguUdtTypeSupport.dropTypeSql( OBJ_TYPE ) );
		safeExecute( st, XuguUdtTypeSupport.dropTypeSql( VAR_TYPE ) );
		safeExecute( st, XuguUdtTypeSupport.dropTypeSql( TAB_TYPE ) );
	}

	private static void safeExecute(Statement st, String sql) {
		try {
			st.execute( sql );
		}
		catch ( Exception ignored ) {
		}
	}
}
