package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.identity.XuguIdentityModeSupport;
import com.xugu.dialect.identity.XuguIdentityModeSupport.IdentityMode;
import com.xugu.dialect.lock.XuguLockTableSupport;
import com.xugu.dialect.lock.XuguLockTableSupport.TableLockMode;
import com.xugu.dialect.lock.XuguLockTableSupport.TableLockWait;
import com.xugu.dialect.pagination.XuguPaginationAlternativesSupport;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Gated IT: A-LCK-006 LOCK TABLE, A-PAG-004 TOP, A-PAG-006 ROWNUM, A-IDN-005 IDENTITY_MODE.
 */
class XuguLockPaginationIdentityIT {

	private static final String LOCK_TABLE = "HIB_I009_P009_LOCK";
	private static final String TOP_TABLE = "HIB_I009_P009_TOP";
	private static final String ROWNUM_TABLE = "HIB_I009_P009_ROWNUM";
	private static final String IDN_TABLE = "HIB_I009_P009_IDN";

	@Test
	void lockTableExclusiveNativeRoundTrip_A_LCK_006() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "drop table if exists " + LOCK_TABLE );
				st.execute( "create table " + LOCK_TABLE + " (id int primary key, balance numeric)" );
				st.execute( "insert into " + LOCK_TABLE + " values (1, 1000)" );

				// lock.md example — EXCLUSIVE MODE within transaction
				st.execute( XuguLockTableSupport.lockTableSql(
						LOCK_TABLE, TableLockMode.EXCLUSIVE, TableLockWait.NONE ) );
				st.execute( "update " + LOCK_TABLE + " set balance = balance - 200 where id = 1" );
				try ( ResultSet rs = st.executeQuery(
						"select balance from " + LOCK_TABLE + " where id = 1" ) ) {
					assertTrue( rs.next() );
					assertEquals( 800, rs.getBigDecimal( 1 ).intValue() );
				}
				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "drop table if exists " + LOCK_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}

	@Test
	void topSyntaxNativeRoundTrip_A_PAG_004() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			try ( Statement st = c.createStatement() ) {
				st.execute( "drop table if exists " + TOP_TABLE );
				st.execute( "create table " + TOP_TABLE + " (id int, name varchar(20))" );
				st.execute( "insert into " + TOP_TABLE + " values (1,'one')(2,'two')(3,'three')(4,'four')" );

				String topSql = XuguPaginationAlternativesSupport.selectTopSql(
						2, "*", TOP_TABLE, "id desc" );
				assertFalse( topSql.toLowerCase().contains( "limit" ) );

				try ( ResultSet rs = st.executeQuery( topSql ) ) {
					assertTrue( rs.next() );
					assertEquals( 4, rs.getInt( "id" ) );
					assertTrue( rs.next() );
					assertEquals( 3, rs.getInt( "id" ) );
					assertFalse( rs.next(), "TOP 2 should return two rows" );
				}
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "drop table if exists " + TOP_TABLE );
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}

	@Test
	void rownumPaginationNativeRoundTrip_A_PAG_006() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			try ( Statement st = c.createStatement() ) {
				st.execute( "drop table if exists " + ROWNUM_TABLE );
				st.execute( "create table " + ROWNUM_TABLE + " (id int, name varchar(20))" );
				st.execute( "insert into " + ROWNUM_TABLE
						+ " values (1,'one')(1,'two')(3,'three')(4,'four')(2,'five')" );

				// select.md §8.3 — WHERE rownum < 4
				try ( ResultSet rs = st.executeQuery(
						XuguPaginationAlternativesSupport.rownumFilterSql( ROWNUM_TABLE, 4 ) ) ) {
					int count = 0;
					while ( rs.next() ) {
						count++;
					}
					assertEquals( 3, count );
				}

				// select.md §8.3 — ORDER BY in subquery, stable ROWNUM
				try ( ResultSet rs = st.executeQuery(
						XuguPaginationAlternativesSupport.rownumOrderedWrapperSql(
								"select * from " + ROWNUM_TABLE + " order by id desc" ) ) ) {
					assertTrue( rs.next() );
					assertEquals( 1, rs.getInt( "rownum" ) );
					assertEquals( 4, rs.getInt( "id" ) );
					assertTrue( rs.next() );
					assertEquals( 2, rs.getInt( "rownum" ) );
					assertEquals( 3, rs.getInt( "id" ) );
				}
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "drop table if exists " + ROWNUM_TABLE );
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}

	@Test
	void identityModeNullAsAutoIncrement_A_IDN_005() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			try ( Statement st = c.createStatement() ) {
				st.execute( "drop table if exists " + IDN_TABLE );
				st.execute( XuguIdentityModeSupport.setIdentityModeSql( IdentityMode.NULL_AS_AUTO_INCREMENT ) );
				st.execute( "create table " + IDN_TABLE + " (id int not null, c1 int identity(1,1))" );

				st.execute( "insert into " + IDN_TABLE + "(id) values(1)" );
				st.execute( "insert into " + IDN_TABLE + " values(2, default)" );
				st.execute( "insert into " + IDN_TABLE + " values(3, null)" );

				try ( ResultSet rs = st.executeQuery( "select id, c1 from " + IDN_TABLE + " order by id" ) ) {
					assertTrue( rs.next() );
					assertEquals( 1, rs.getInt( 2 ) );
					assertTrue( rs.next() );
					assertEquals( 2, rs.getInt( 2 ) );
					assertTrue( rs.next() );
					assertEquals( 3, rs.getInt( 2 ) );
				}

				st.execute( XuguIdentityModeSupport.setIdentityModeSql( IdentityMode.ZERO_AS_AUTO_INCREMENT ) );
				st.execute( "insert into " + IDN_TABLE + " values(4, 0)" );
				st.execute( "insert into " + IDN_TABLE + " values(5, null)" );

				try ( ResultSet rs = st.executeQuery(
						"select c1 from " + IDN_TABLE + " where id in (4, 5) order by id" ) ) {
					assertTrue( rs.next() );
					assertEquals( 4, rs.getInt( 1 ) );
					assertTrue( rs.next() );
					assertEquals( 5, rs.getInt( 1 ) );
				}
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "drop table if exists " + IDN_TABLE );
					st.execute( XuguIdentityModeSupport.setIdentityModeSql( IdentityMode.DEFAULT ) );
				}
				catch ( Exception ignored ) {
				}
			}
		}
	}
}
