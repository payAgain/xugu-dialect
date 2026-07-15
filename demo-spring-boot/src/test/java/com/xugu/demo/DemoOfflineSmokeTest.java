package com.xugu.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.xugu.demo.entity.DemoPerson;

import jakarta.persistence.Table;

/**
 * Offline smoke: no live DB, no Spring context. Default {@code mvn test} stays green.
 */
class DemoOfflineSmokeTest {

	@Test
	void demoPersonTableUsesHibDemoPrefix() {
		Table table = DemoPerson.class.getAnnotation( Table.class );
		assertEquals( "HIB_DEMO_PERSON", table.name() );
		assertTrue( table.name().startsWith( "HIB_DEMO_" ) );
	}

	@Test
	void applicationYmlDocumentsExplicitDialectAndEnvKeys() throws Exception {
		try ( InputStream in = DemoApplication.class.getClassLoader()
				.getResourceAsStream( "application.yml" ) ) {
			assertTrue( in != null, "application.yml missing from classpath" );
			String yaml = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
			assertTrue( yaml.contains( "com.xugu.dialect.XuguDialect" ) );
			assertTrue( yaml.contains( "XUGU_JDBC_URL" ) );
			assertTrue( yaml.contains( "compatiblemode=NONE" ) );
			assertTrue( yaml.contains( "com.xugu.cloudjdbc.Driver" ) );
		}
	}
}
