package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoJsonDoc;
import com.xugu.demo.entity.DemoTypedSample;
import com.xugu.demo.repository.DemoJsonDocRepository;
import com.xugu.demo.repository.DemoTypedSampleRepository;
import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: I-008 Q3 out-of-box Boot path — default {@code application.yml} + classpath deps
 * (Jackson FormatMapper, UUID converter, {@code preferred_uuid_jdbc_type}, JSON HQL flag).
 * No extra Hibernate property overrides beyond disabling startup CRUD.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoUuidJsonOutOfBoxIT {

	static {
		if ( XuguIntegrationGate.isEnabled() ) {
			try {
				DemoXuguJdbc.recreateTypedSampleTable();
			}
			catch ( SQLException e ) {
				throw new ExceptionInInitializerError( e );
			}
		}
	}

	@Autowired
	private Environment environment;

	@Autowired
	private DemoTypedSampleRepository typedSampleRepository;

	@Autowired
	private DemoJsonDocRepository jsonDocRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		typedSampleRepository.deleteAll();
		jsonDocRepository.deleteAll();
	}

	@Test
	void defaultApplicationYmlExposesUuidJsonChecklist() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		assertEquals(
				"VARCHAR",
				environment.getProperty( "spring.jpa.properties.hibernate.type.preferred_uuid_jdbc_type" ) );
		assertEquals(
				"true",
				environment.getProperty( "spring.jpa.properties.hibernate.query.hql.json_functions_enabled" ) );
		assertEquals(
				"com.xugu.dialect.XuguDialect",
				environment.getProperty( "spring.jpa.properties.hibernate.dialect" ) );
	}

	@Test
	void uuidAndJsonGoldenPathWithDefaultBootWiring() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		UUID guid = UUID.fromString( "6ba7b810-9dad-11d1-80b4-00c04fd430c8" );
		DemoTypedSample typed = typedSampleRepository.save( new DemoTypedSample(
				1,
				java.math.BigDecimal.ONE,
				"out-of-box",
				true,
				java.time.LocalDate.of( 2026, 7, 20 ),
				java.time.LocalDateTime.of( 2026, 7, 20, 17, 0 ),
				new byte[] { 0x01 },
				new byte[] { 0x02 },
				guid
		) );
		DemoJsonDoc jsonDoc = jsonDocRepository.save( new DemoJsonDoc( "q3", "{\"source\":\"out-of-box\"}" ) );
		typedSampleRepository.flush();
		jsonDocRepository.flush();
		entityManager.clear();

		DemoTypedSample loadedTyped = typedSampleRepository.findById( typed.getId() )
				.orElseThrow( () -> new AssertionError( "typed sample missing" ) );
		assertEquals( guid, loadedTyped.getGuidVal() );

		DemoJsonDoc loadedJson = jsonDocRepository.findById( jsonDoc.getId() )
				.orElseThrow( () -> new AssertionError( "json doc missing" ) );
		assertNotNull( loadedJson.getPayload() );
		String payload = loadedJson.getPayload().toLowerCase( Locale.ROOT );
		assertTrue( payload.contains( "out-of-box" ) || payload.contains( "source" ), "payload=" + payload );

		String arr = entityManager.createQuery(
						"select cast(json_arrayagg(d.label) as string) from DemoJsonDoc d "
								+ "where d.id = :id",
						String.class )
				.setParameter( "id", jsonDoc.getId() )
				.getSingleResult();
		assertNotNull( arr );
		assertTrue( arr.contains( "q3" ) || arr.contains( "Q3" ), "arrayagg should include label: " + arr );
	}
}
