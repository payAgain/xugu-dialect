package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I010P004PointEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguGeometricTypeSupport;
import com.xugu.dialect.type.XuguPointJdbcType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-017 geometric types (native + POINT entity ORM) + A-FUN-020 functions.
 *
 * <p>SQL shapes from {@code reference/sql/datatype/geometric.md} and
 * {@code reference/function/geometric-functions/{area,center,point,box,circle}.md}.
 * Simple 2D types only — not PostGIS. Entity path (I-010/P-004) uses
 * {@link XuguPointJdbcType} for {@code String} + {@code @JdbcTypeCode(POINT|GEOMETRY)}.
 * Non-POINT subtypes remain native/tooling only.
 */
class XuguGeometricTypeAndFunctionsIT {

	private static final String TYPE_TABLE = "HIB_I009_P004_GEOM";
	private static final String ENTITY_TABLE = "HIB_I010_P004_POINT";

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
	void pointEntityOrmRoundTrip_A_TYP_017() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanupEntityTable();

		// geometric.md §点 — POINT literal (x,y); GEOMETRY→POINT DDL alias uses same shape
		final String pointLiteral = "(1,1)";
		final String geometryLiteral = "(2,3)";

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P004PointEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P004PointEntity( 1, pointLiteral, geometryLiteral ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I010P004PointEntity loaded = session.find( I010P004PointEntity.class, 1 );
				assertNotNull( loaded, "entity must load" );
				assertNotNull( loaded.getPoint(), "POINT column" );
				assertNotNull( loaded.getGeometry(), "GEOMETRY→POINT column" );
				assertTrue( containsCoords( loaded.getPoint(), "1", "1" ),
						"POINT round-trip: " + loaded.getPoint() );
				assertTrue( containsCoords( loaded.getGeometry(), "2", "3" ),
						"GEOMETRY→POINT round-trip: " + loaded.getGeometry() );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "POINT entity ORM IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanupEntityTable();
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

	private static boolean containsCoords(String value, String x, String y) {
		return value != null && value.contains( x ) && value.contains( y );
	}

	private static StandardServiceRegistry buildRegistry() {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, completion -> {
		} );
	}

	private static void cleanupEntityTable() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + ENTITY_TABLE );
		}
		catch ( Exception ignored ) {
		}
	}
}
