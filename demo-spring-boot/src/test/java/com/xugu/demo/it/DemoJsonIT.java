package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoJsonDoc;
import com.xugu.demo.repository.DemoJsonDocRepository;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: JSON column round-trip + one aggregate (A-TYP-013, C-JSON-001).
 * Shares {@link DemoJsonDoc} with A-FUN-017 in {@link DemoFunctionsIT}.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false",
		"spring.jpa.properties.hibernate.query.hql.json_functions_enabled=true"
})
@Transactional
class DemoJsonIT {

	@Autowired
	private DemoJsonDocRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void jsonColumnRoundTripAndArrayAgg() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoJsonDoc a = repository.save( new DemoJsonDoc( "a", "{\"n\":1}" ) );
		DemoJsonDoc b = repository.save( new DemoJsonDoc( "b", "{\"n\":2}" ) );
		repository.flush();
		entityManager.clear();

		DemoJsonDoc loaded = repository.findById( a.getId() )
				.orElseThrow( () -> new AssertionError( "json doc missing" ) );
		assertNotNull( loaded.getPayload() );
		String payload = loaded.getPayload().toLowerCase( Locale.ROOT );
		assertTrue( payload.contains( "1" ) || payload.contains( "n" ), "payload=" + loaded.getPayload() );

		String arr = entityManager.createQuery(
						"select cast(json_arrayagg(d.label) as string) from DemoJsonDoc d "
								+ "where d.id in (:ids)",
						String.class )
				.setParameter( "ids", List.of( a.getId(), b.getId() ) )
				.getSingleResult();
		assertNotNull( arr );
		assertTrue( arr.contains( "a" ) || arr.contains( "A" ), "arrayagg should include a: " + arr );
		assertTrue( arr.contains( "b" ) || arr.contains( "B" ), "arrayagg should include b: " + arr );
	}
}
