package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: Spring Boot + JPA persist/find against real XuguDB.
 * Default {@code mvn test} skips (gate off). Enable with
 * {@code -Dxugu.run.integration=true} or env {@code XUGU_RUN_IT=true}.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoPersonCrudIT {

	@Autowired
	private DemoPersonRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void persistAndFindPerson() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson saved = repository.save( new DemoPerson( "it-person" ) );
		assertNotNull( saved.getId(), "IDENTITY id must be backfilled" );
		assertTrue( saved.getId() > 0 );

		repository.flush();
		entityManager.clear();

		DemoPerson found = repository.findById( saved.getId() )
				.orElseThrow( () -> new AssertionError( "findById returned empty" ) );
		assertEquals( "it-person", found.getName() );
		assertEquals( saved.getId(), found.getId() );
	}
}
