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
import org.opentest4j.TestAbortedException;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I010P003XmlEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguXmlJdbcType;
import com.xugu.dialect.type.XuguXmlTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-016 XML type (native + entity ORM) + A-FUN-021 XML functions.
 *
 * <p>SQL shapes from {@code reference/sql/datatype/xml.md} and
 * {@code reference/function/xml-functions/{extract,xmlelement,xmlquery,xmltable}.md}.
 * {@code XMLTABLE} is documented single-node only ({@code xmltable.md} note) — IT uses
 * native SQL with empty→assumption skip; never fail as covered-live / cluster-safe.
 *
 * <p>Entity path (I-010/P-003) uses {@link XuguXmlJdbcType} for
 * {@code String} + {@code @JdbcTypeCode(SQLXML)} persist/load.
 *
 * <p>HQL Session path (I-010/P-005 / A-FUN-021): {@code Session.createQuery} positive
 * for {@code xmlelement} / {@code xmlquery}; XMLTABLE remains native known-limit only.
 */
class XuguXmlTypeAndFunctionsIT {

	private static final String TYPE_TABLE = "HIB_I009_P003_XML";
	private static final String ENTITY_TABLE = "HIB_I010_P003_XML";
	private static final String EXTRACT_TABLE = "HIB_I009_P003_EXTRACT";
	private static final String XMLTABLE_TABLE = "HIB_I009_P003_XMLTABLE";

	@Test
	void xmlTypeNativeRoundTrip_A_TYP_016() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		assertEquals( "xml", XuguXmlTypeSupport.XML_DDL, "A-TYP-016" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {
				st.execute( "DROP TABLE IF EXISTS " + TYPE_TABLE );
				st.execute( """
						CREATE TABLE %s (
						  c_id INT PRIMARY KEY,
						  c_xml XML NOT NULL
						)
						""".formatted( TYPE_TABLE ) );

				st.execute( """
						INSERT INTO %s VALUES (1, '<num>1</num>')(2, '<str>ab</str>')
						""".formatted( TYPE_TABLE ) );

				try ( ResultSet rs = st.executeQuery( "SELECT c_id, c_xml FROM " + TYPE_TABLE + " ORDER BY c_id" ) ) {
					assertTrue( rs.next() );
					assertEquals( 1, rs.getInt( 1 ) );
					assertEquals( "<num>1</num>", rs.getString( 2 ).trim() );
					assertTrue( rs.next() );
					assertEquals( 2, rs.getInt( 1 ) );
					assertEquals( "<str>ab</str>", rs.getString( 2 ).trim() );
				}

				st.execute( "UPDATE %s SET c_xml='<num>2</num>' WHERE c_id = 1".formatted( TYPE_TABLE ) );
				try ( ResultSet rs = st.executeQuery(
						"SELECT c_xml FROM " + TYPE_TABLE + " WHERE c_id = 1" ) ) {
					assertTrue( rs.next() );
					assertEquals( "<num>2</num>", rs.getString( 1 ).trim() );
				}

				st.execute( "DELETE FROM %s WHERE c_id = 1".formatted( TYPE_TABLE ) );
				try ( ResultSet rs = st.executeQuery( "SELECT COUNT(*) FROM " + TYPE_TABLE ) ) {
					assertTrue( rs.next() );
					assertEquals( 1, rs.getInt( 1 ) );
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
			fail( "XML type native IT failed: " + e.getMessage(), e );
		}
	}

	@Test
	void xmlEntityOrmRoundTrip_A_TYP_016() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanupEntityTable();

		final String payload = "<num>1</num>";

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P003XmlEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P003XmlEntity( 1, payload ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				I010P003XmlEntity loaded = session.find( I010P003XmlEntity.class, 1 );
				assertNotNull( loaded, "entity must load" );
				assertNotNull( loaded.getXml(), "SQLXML column" );
				assertEquals( payload, loaded.getXml().trim(), "SQLXML String round-trip" );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "XML entity ORM IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanupEntityTable();
		}
	}

	/**
	 * I-010/P-005: HQL {@code Session.createQuery} live positive for {@code xmlelement}
	 * and {@code xmlquery}. Reuses {@link I010P003XmlEntity} XML column for xmlquery PASSING.
	 * XMLTABLE is intentionally not exercised here (native known-limit only).
	 */
	@Test
	void xmlFunctionsHqlSession_A_FUN_021() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		cleanupEntityTable();

		final String payload = "<PDRecord><PDName>Daniel Morgan</PDName></PDRecord>";

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I010P003XmlEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I010P003XmlEntity( 1, payload ) );
				session.getTransaction().commit();
			}

			try ( Session session = sf.openSession() ) {
				// XMLELEMENT — HQL named function → XMLELEMENT(xmlname[, xmlvalue])
				String element = session.createQuery(
						"select xmlelement('name', 'xxx')", String.class )
						.getSingleResult();
				assertNotNull( element );
				assertTrue( element.contains( "name" ) && element.contains( "xxx" ),
						"XMLELEMENT HQL result: " + element );

				// XMLQUERY — HQL pattern → xmlquery(?1 PASSING ?2 RETURNING CONTENT)
				String query = session.createQuery(
						"select xmlquery('/PDRecord/PDName', e.xml) from I010P003XmlEntity e where e.id = 1",
						String.class )
						.getSingleResult();
				assertNotNull( query );
				assertTrue( query.contains( "Daniel Morgan" ), "XMLQUERY HQL result: " + query );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "XML HQL Session IT failed: " + e.getMessage(), e );
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
	void xmlFunctionsNativeSubset_A_FUN_021() throws Exception {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		try ( Connection c = XuguTestConnection.open() ) {
			c.setAutoCommit( false );
			try ( Statement st = c.createStatement() ) {

				// EXTRACT — extract.md
				st.execute( "DROP TABLE IF EXISTS " + EXTRACT_TABLE );
				st.execute( """
						CREATE TABLE %s (id INT PRIMARY KEY, xml_col XML NOT NULL)
						""".formatted( EXTRACT_TABLE ) );
				st.execute( """
						INSERT INTO %s VALUES (2, '<country>china<city capital="true">beijing</city><city capital="false">shanghai</city></country>')
						""".formatted( EXTRACT_TABLE ) );
				try ( ResultSet rs = st.executeQuery( """
						SELECT EXTRACT(xml_col,'/country/city[@capital="true"]')
						FROM %s WHERE id = 2
						""".formatted( EXTRACT_TABLE ) ) ) {
					assertTrue( rs.next(), "EXTRACT row expected" );
					String extracted = rs.getString( 1 );
					assertNotNull( extracted );
					assertTrue( extracted.contains( "beijing" ), "EXTRACT result: " + extracted );
				}

				// XMLELEMENT — xmlelement.md
				try ( ResultSet rs = st.executeQuery( "SELECT XMLELEMENT(\"name\",'xxx') FROM DUAL" ) ) {
					assertTrue( rs.next() );
					String element = rs.getString( 1 );
					assertNotNull( element );
					assertTrue( element.contains( "name" ) && element.contains( "xxx" ),
							"XMLELEMENT result: " + element );
				}

				// XMLQUERY — xmlquery.md (XMLTYPE cast per doc example)
				try ( ResultSet rs = st.executeQuery( """
						SELECT XMLQuery(
						  '/PDRecord/PDName'
						  PASSING XMLTYPE('<PDRecord><PDName>Daniel Morgan</PDName></PDRecord>')
						  RETURNING CONTENT) FROM DUAL
						""" ) ) {
					assertTrue( rs.next() );
					String query = rs.getString( 1 );
					assertNotNull( query );
					assertTrue( query.contains( "Daniel Morgan" ), "XMLQUERY result: " + query );
				}

				// XMLTABLE — xmltable.md (single-node note in doc; native SQL only)
				st.execute( "DROP TABLE IF EXISTS " + XMLTABLE_TABLE );
				st.execute( """
						CREATE TABLE %s (id INT PRIMARY KEY, xml_col XML NOT NULL, insert_time DATE)
						""".formatted( XMLTABLE_TABLE ) );
				st.execute( """
						INSERT INTO %s VALUES (1, '<?xml version="1.0" encoding="UTF-8"?>
						<bookstore>
						<book category="CHILDREN">
						<title lang="en">Harry Potter</title>
						<author>J K.Rowling</author>
						<year>2005</year>
						<price>29.99</price>
						</book>
						</bookstore>', DATE '2020-03-01')
						""".formatted( XMLTABLE_TABLE ) );
				try ( ResultSet rs = st.executeQuery( """
						SELECT t2."title", t2."author", t2."year", t2."price"
						FROM %s t1, XMLTABLE(
						  '//book[@category="CHILDREN"]'
						  PASSING t1.xml_col
						  COLUMNS "title" VARCHAR PATH 'title',
						    "author" VARCHAR PATH 'author',
						    "year" VARCHAR PATH 'year',
						    "price" VARCHAR PATH 'price'
						) t2
						WHERE t1.id = 1
						""".formatted( XMLTABLE_TABLE ) ) ) {
					// xmltable.md: XMLTABLE is single-node only — empty/no-op on some cluster topologies
					Assumptions.assumeTrue(
							rs.next(),
							"A-FUN-021 known-limit: XMLTABLE returned no rows (xmltable.md: single-node only; not cluster-safe)" );
					assertEquals( "Harry Potter", rs.getString( 1 ).trim() );
					assertTrue( rs.getString( 2 ).contains( "Rowling" ), "author=" + rs.getString( 2 ) );
					assertEquals( "2005", rs.getString( 3 ).trim() );
					assertEquals( "29.99", rs.getString( 4 ).trim() );
				}

				c.commit();
			}
			catch ( Exception e ) {
				c.rollback();
				throw e;
			}
			finally {
				try ( Statement st = c.createStatement() ) {
					st.execute( "DROP TABLE IF EXISTS " + EXTRACT_TABLE );
					st.execute( "DROP TABLE IF EXISTS " + XMLTABLE_TABLE );
					c.commit();
				}
				catch ( Exception ignored ) {
				}
			}
		}
		catch ( TestAbortedException abort ) {
			throw abort;
		}
		catch ( Exception e ) {
			fail( "XML functions native IT failed: " + e.getMessage(), e );
		}
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
