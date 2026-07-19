package com.xugu.dialect.it;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.QuerySettings;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.support.XuguITGate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: C-JSON-005 bounded json_* HQL subset beyond A-FUN-017 value/extract.
 */
class XuguJsonSubsetDeepenIT {

	@Test
	void jsonSubsetDeepen_Hql_C_JSON_005() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		StandardServiceRegistry registry = buildRegistry();
		SessionFactory sf = null;
		try {
			sf = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
			try ( Session session = sf.openSession() ) {
				String doc = "{\"a\":1,\"b\":\"x\",\"c\":[10,20]}";

				String unquoted = session.createQuery(
						"select json_unquote('\"hello\"')", String.class ).getSingleResult();
				assertEquals( "hello", unquoted );

				Integer len = session.createQuery(
						"select json_length(:doc)", Integer.class )
						.setParameter( "doc", doc ).getSingleResult();
				assertEquals( 3, len );

				Integer lenPath = session.createQuery(
						"select json_length(:doc, '$.c')", Integer.class )
						.setParameter( "doc", doc ).getSingleResult();
				assertEquals( 2, lenPath );

				String typeRoot = session.createQuery(
						"select json_type('{\"a\":1}')", String.class ).getSingleResult();
				assertNotNull( typeRoot );
				assertEquals( "OBJECT", typeRoot.toUpperCase() );

				// Baseline subset still works alongside deepen
				String jv = session.createQuery(
						"select json_value(:doc, '$.b')", String.class )
						.setParameter( "doc", doc ).getSingleResult();
				assertEquals( "x", jv );
			}
		}
		catch ( Exception e ) {
			fail( "C-JSON-005 JSON subset deepen IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	private static StandardServiceRegistry buildRegistry() {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, com.xugu.dialect.support.XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, com.xugu.dialect.support.XuguTestConnection.DRIVER )
				.applySetting( QuerySettings.JSON_FUNCTIONS_ENABLED, "true" )
				.build();
	}
}
