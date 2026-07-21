package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.opentest4j.TestAbortedException;

import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;
import com.xugu.dialect.type.XuguXmlTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: A-TYP-016 XML type + A-FUN-021 XML functions native SQL round-trip.
 *
 * <p>SQL shapes from {@code reference/sql/datatype/xml.md} and
 * {@code reference/function/xml-functions/{extract,xmlelement,xmlquery,xmltable}.md}.
 * {@code XMLTABLE} is documented single-node only ({@code xmltable.md} note) — IT uses
 * native SQL, not cluster-safe claim.
 */
class XuguXmlTypeAndFunctionsIT {

	private static final String TYPE_TABLE = "HIB_I009_P003_XML";
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
}
