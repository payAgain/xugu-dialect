package com.xugu.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import com.xugu.demo.entity.DemoDept;
import com.xugu.demo.entity.DemoDeptMember;
import com.xugu.demo.entity.DemoJsonDoc;
import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.entity.DemoSeqTicket;
import com.xugu.demo.entity.DemoTypedSample;

import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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

	/** Layer B offline surface: association + SEQUENCE entity annotations (HIB_DEMO_). */
	@Test
	void layerBEntitiesUseHibDemoPrefixAndSequence() {
		Table dept = DemoDept.class.getAnnotation( Table.class );
		Table member = DemoDeptMember.class.getAnnotation( Table.class );
		Table ticket = DemoSeqTicket.class.getAnnotation( Table.class );
		assertEquals( "HIB_DEMO_DEPT", dept.name() );
		assertEquals( "HIB_DEMO_DEPT_MEMBER", member.name() );
		assertEquals( "HIB_DEMO_SEQ_TICKET", ticket.name() );
		assertTrue( dept.name().startsWith( "HIB_DEMO_" ) );
		assertTrue( member.name().startsWith( "HIB_DEMO_" ) );
		assertTrue( ticket.name().startsWith( "HIB_DEMO_" ) );

		UniqueConstraint[] uniques = member.uniqueConstraints();
		assertTrue( uniques.length >= 1, "A-SCH-011: UNIQUE on association child" );
		assertEquals( "UK_HIB_DEMO_DEPT_MEMBER_CODE", uniques[0].name() );

		SequenceGenerator seq = DemoSeqTicket.class.getAnnotation( SequenceGenerator.class );
		assertEquals( "HIB_DEMO_SEQ_TICKET_SEQ", seq.sequenceName() );
		try {
			var idField = DemoSeqTicket.class.getDeclaredField( "id" );
			var generated = idField.getAnnotation( jakarta.persistence.GeneratedValue.class );
			assertEquals( GenerationType.SEQUENCE, generated.strategy() );
		}
		catch ( NoSuchFieldException e ) {
			throw new AssertionError( "DemoSeqTicket.id missing", e );
		}
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
			assertTrue( yaml.contains( "flyway:" ), "Flyway config documented (B-FLY-001)" );
			assertTrue( yaml.contains( "enabled: false" ), "Flyway default off for offline IT" );
		}
	}

	/** I-007 P-005 Track B: Flyway Xugu plugin + migration resource (offline; no DB). */
	@Test
	void flywayXuguPluginAndMigrationOnClasspath() throws Exception {
		String pluginResource = "META-INF/services/org.flywaydb.core.extensibility.Plugin";
		try ( InputStream in = DemoApplication.class.getClassLoader().getResourceAsStream( pluginResource ) ) {
			assertTrue( in != null, pluginResource + " missing" );
			String body = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
			assertTrue( body.contains( "com.xugu.demo.flyway.XuguFlywayDatabaseType" ), body );
		}
		try ( InputStream in = DemoApplication.class.getClassLoader()
				.getResourceAsStream( "db/migration/V1__hib_demo_flyway_marker.sql" ) ) {
			assertTrue( in != null, "V1 Flyway migration missing from classpath" );
			String sql = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
			assertTrue( sql.contains( "HIB_DEMO_FLYWAY_MARKER" ), sql );
		}
	}

	/**
	 * Offline SPI packaging smoke (A-SPI-002 support): dialect jar registers
	 * {@code DialectResolver} via META-INF/services. Live Boot resolve is
	 * {@code DemoSpiDialectAutoResolveIT} under the IT gate.
	 */
	@Test
	void dialectResolverServicesFileOnClasspath() throws Exception {
		String resource = "META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver";
		try ( InputStream in = DemoApplication.class.getClassLoader().getResourceAsStream( resource ) ) {
			assertTrue( in != null, resource + " missing from demo classpath (xugu-dialect jar)" );
			String body = new String( in.readAllBytes(), StandardCharsets.UTF_8 );
			assertTrue( body.contains( "com.xugu.dialect.XuguDialectResolver" ), body );
		}
	}

	/** Layer C′ offline surface: typed sample + JSON doc tables use HIB_DEMO_ prefix. */
	@Test
	void layerCEntitiesUseHibDemoPrefix() {
		Table typed = DemoTypedSample.class.getAnnotation( Table.class );
		Table json = DemoJsonDoc.class.getAnnotation( Table.class );
		assertEquals( "HIB_DEMO_TYPED_SAMPLE", typed.name() );
		assertEquals( "HIB_DEMO_JSON_DOC", json.name() );
		assertTrue( typed.name().startsWith( "HIB_DEMO_" ) );
		assertTrue( json.name().startsWith( "HIB_DEMO_" ) );
	}
}
