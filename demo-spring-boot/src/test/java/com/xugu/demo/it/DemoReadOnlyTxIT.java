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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.XuguIntegrationGate;

/**
 * Gated IT: {@code @Transactional(readOnly=true)} smoke (B-DEMO-003, optional Track B).
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
class DemoReadOnlyTxIT {

	@Autowired
	private DemoPersonRepository repository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void readOnlyTransactionQueriesPersistedRow() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson saved = repository.save( new DemoPerson( "ro-smoke" ) );
		repository.flush();
		Long id = saved.getId();
		assertNotNull( id );

		TransactionTemplate readOnly = new TransactionTemplate( transactionManager );
		readOnly.setReadOnly( true );
		String name = readOnly.execute( status -> repository.findById( id )
				.map( DemoPerson::getName )
				.orElse( null ) );

		assertEquals( "ro-smoke", name );
	}
}
