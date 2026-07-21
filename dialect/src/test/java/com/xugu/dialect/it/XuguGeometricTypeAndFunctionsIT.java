package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguGeometricTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-017 geometric types + A-FUN-020 geometric functions native SQL.
 *
 * <p>SQL shapes from {@code reference/sql/datatype/geometric.md} and
 * {@code reference/function/geometric-functions/{area,center,point,box,circle}.md}.
 * Simple 2D types only — not PostGIS; ORM entity mapping remains known-limit.
 */
class XuguGeometricTypeAndFunctionsIT {

	private static final String TYPE_TABLE = "HIB_I009_P004_GEOM";

	@Test
	void geometricTypesNativeRoundTrip_A_TYP_017() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		assertEquals( "point", XuguGeometricTypeSupport.POINT_DDL, "A-TYP-017" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TYPE_TABLE );
				// geometric.md §示例 — all seven documented column types
				st.execute( """
						CREATE TABLE %s (
						  c_id INT PRIMARY KEY,
						  c_pt POINT,
						  c_le LINE,
						  c_lg LSEG,
						  c_bx BOX,
						  c_ce CIRCLE,
						  c_ph PATH,
						  c_pn POLYGON
						)
						""".formatted( TYPE_TABLE ) );

				st.execute( """
						INSERT INTO %s VALUES (
						  1,
						  '(1,1)',
						  '(1,1),(2,2)',
						  '(1,1),(3,3)',
						  '(1,1),(4,4)',
						  '<(1,2),3>',
						  '(1,1),(2,3),(3,1)',
						  '(1,1),(2,3),(3,1),(1,1)'
						)
						""".formatted( TYPE_TABLE ) );

				try ( ResultSet rs = st.executeQuery( "SELECT * FROM " + TYPE_TABLE + " WHERE c_id = 1" ) ) {
					assertTrue( rs.next(), "expected geometric row" );
					assertEquals( 1, rs.getInt( "c_id" ) );
					assertNotNull( rs.getString( "c_pt" ) );
					assertNotNull( rs.getString( "c_le" ) );
					assertNotNull( rs.getString( "c_lg" ) );
					assertNotNull( rs.getString( "c_bx" ) );
					assertNotNull( rs.getString( "c_ce" ) );
					assertNotNull( rs.getString( "c_ph" ) );
					assertNotNull( rs.getString( "c_pn" ) );
				}

				st.execute( "UPDATE %s SET c_pt='(0,1)' WHERE c_id = 1".formatted( TYPE_TABLE ) );
				try ( ResultSet rs = st.executeQuery(
						"SELECT c_pt FROM " + TYPE_TABLE + " WHERE c_id = 1" ) ) {
					assertTrue( rs.next() );
					assertTrue( rs.getString( 1 ).contains( "0" ) && rs.getString( 1 ).contains( "1" ),
							"updated point: " + rs.getString( 1 ) );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "DROP TABLE IF EXISTS " + TYPE_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( Exception e ) {
			fail( "Geometric type native IT failed: " + e.getMessage(), e );
		}
	}

	@Test
	void geometricFunctionsNativeSubset_A_FUN_020() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {

				// area.md — CIRCLE area
				try ( ResultSet rs = st.executeQuery( "SELECT AREA(CIRCLE('((5, 0), 1)')) FROM DUAL" ) ) {
					assertTrue( rs.next(), "AREA(CIRCLE) row expected" );
					double area = rs.getDouble( 1 );
					assertTrue( area > 3.0 && area < 3.2, "AREA circle ≈ π, got " + area );
				}

				// center.md — BOX center
				try ( ResultSet rs = st.executeQuery( "SELECT CENTER(BOX('(1, 2), (0, 0)')) FROM DUAL" ) ) {
					assertTrue( rs.next() );
					String center = rs.getString( 1 );
					assertNotNull( center );
					assertTrue( center.contains( "0.5" ) && center.contains( "1" ),
							"CENTER(BOX): " + center );
				}

				// point.md — coordinate construct
				try ( ResultSet rs = st.executeQuery( "SELECT POINT(23.4, -44.5) FROM DUAL" ) ) {
					assertTrue( rs.next() );
					String pt = rs.getString( 1 );
					assertNotNull( pt );
					assertTrue( pt.contains( "23.4" ) && pt.contains( "-44.5" ), "POINT: " + pt );
				}

				// box.md — two points to box
				try ( ResultSet rs = st.executeQuery(
						"SELECT BOX(POINT('(0, 1)'), POINT('(1, 0)')) FROM DUAL" ) ) {
					assertTrue( rs.next() );
					String box = rs.getString( 1 );
					assertNotNull( box );
					assertTrue( box.contains( "1" ) && box.contains( "0" ), "BOX: " + box );
				}

				// circle.md — point + radius
				try ( ResultSet rs = st.executeQuery(
						"SELECT CIRCLE(POINT('(3, 4)'), 2.0) FROM DUAL" ) ) {
					assertTrue( rs.next() );
					String circle = rs.getString( 1 );
					assertNotNull( circle );
					assertTrue( circle.contains( "3" ) && circle.contains( "4" ),
							"CIRCLE: " + circle );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
		}
		catch ( Exception e ) {
			fail( "Geometric functions native IT failed: " + e.getMessage(), e );
		}
	}
}
