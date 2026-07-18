package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import com.xugu.demo.DemoStartupCrudRunner;
import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.XuguIntegrationGate;

/**
 * Gated IT: live path for {@link DemoStartupCrudRunner} with {@code startup-crud=true}.
 * Other ITs force {@code startup-crud=false}; this class asserts the ApplicationRunner path.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=true"
})
class DemoStartupCrudIT {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DemoPersonRepository repository;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void startupCrudRunnerPersistsAndFindsPerson() {
		assertTrue( XuguIntegrationGate.isEnabled() );
		assertNotNull(
				applicationContext.getBean( DemoStartupCrudRunner.class ),
				"DemoStartupCrudRunner must be registered when startup-crud=true" );

		boolean found = repository.findAll().stream()
				.map( DemoPerson::getName )
				.anyMatch( "startup-demo"::equals );
		assertTrue( found,
				"ApplicationRunner must persist/find a HIB_DEMO_PERSON named startup-demo" );
	}
}
