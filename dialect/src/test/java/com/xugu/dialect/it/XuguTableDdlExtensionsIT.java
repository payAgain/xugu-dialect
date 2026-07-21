package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.ddl.XuguTableDdlSupport;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-DDL-008 PARTITION BY + A-DDL-009 ENCRYPT BY native DDL.
 *
 * <p>SQL shapes from {@code reference/object/table/partition.md} and
 * {@code reference/object/table/create.md}. ENCRYPT IT skips when no encryptor is visible
 * (SYSSSO prerequisite per {@code reference/object/encryptor.md}).
 */
class XuguTableDdlExtensionsIT {

	private static final String LIST_TABLE = "HIB_I009_P007_LIST_PART";
	private static final String ENCRYPT_TABLE = "HIB_I009_P007_ENCRYPT";

	@Test
	void listPartitionNativeRoundTrip_A_DDL_008() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "drop table if exists " + LIST_TABLE );

				// partition.md §1.3 example 1 — LIST (city) with OTHERVALUES catch-all
				st.execute( XuguTableDdlSupport.createListPartitionTableSql(
						LIST_TABLE,
						"id int,\nname varchar(20),\ncity char(20)",
						"city",
						"('四川'),('云南'),('贵州'),(othervalues)"
				) );

				try ( ResultSet rs = st.executeQuery(
						"select ut.parti_key from sys_tables ut where ut.table_name='"
								+ LIST_TABLE + "'" ) ) {
					assertTrue( rs.next(), "partition table metadata expected" );
					String partiKey = rs.getString( 1 );
					assertNotNull( partiKey );
					assertTrue(
							partiKey.toUpperCase().contains( "CITY" ),
							"parti_key should reference city: " + partiKey );
				}

				st.execute( "insert into " + LIST_TABLE + " values (1, 'a', '四川')" );
				st.execute( "insert into " + LIST_TABLE + " values (2, 'b', '北京')" );
				try ( ResultSet rs = st.executeQuery(
						"select count(*) from " + LIST_TABLE + " where city in ('四川', '北京')" ) ) {
					assertTrue( rs.next() );
					assertEquals( 2, rs.getInt( 1 ) );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "drop table if exists " + LIST_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "LIST partition native IT failed: " + e.getMessage(), e );
		}
	}

	@Test
	void encryptByNativeWhenEncryptorAvailable_A_DDL_009() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				String encryptor = findFirstEncryptorName( st );
				Assumptions.assumeTrue(
						encryptor != null,
						"A-DDL-009 known-limit: no encryptor visible — SYSSSO CREATE ENCRYPTOR prerequisite" );

				st.execute( "drop table if exists " + ENCRYPT_TABLE );
				st.execute( XuguTableDdlSupport.createTableWithEncryptBySql(
						ENCRYPT_TABLE,
						"id int primary key,\npayload varchar(32)",
						encryptor
				) );
				st.execute( "insert into " + ENCRYPT_TABLE + " values (1, 'secret')" );
				try ( ResultSet rs = st.executeQuery(
						"select payload from " + ENCRYPT_TABLE + " where id = 1" ) ) {
					assertTrue( rs.next(), "ENCRYPT BY table row expected" );
					assertEquals( "secret", rs.getString( 1 ).trim() );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "drop table if exists " + ENCRYPT_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "ENCRYPT BY native IT failed: " + e.getMessage(), e );
		}
	}

	private static String findFirstEncryptorName(Statement st) throws Exception {
		try ( ResultSet rs = st.executeQuery( "select name from sys_encryptors limit 1" ) ) {
			if ( rs.next() ) {
				String name = rs.getString( 1 );
				return name == null ? null : name.trim();
			}
		}
		catch ( Exception ignored ) {
			// sys_encryptors may be inaccessible without ACL_SSO — honest known-limit skip
		}
		return null;
	}
}
