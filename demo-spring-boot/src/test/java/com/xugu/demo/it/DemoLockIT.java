package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

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
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: pessimistic write lock + NOWAIT timeout representative (A-LCK-001 / A-LCK-003).
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoLockIT {

	@Autowired
	private DemoPersonRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void pessimisticWriteLocksPersonRow() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson saved = repository.save( new DemoPerson( "lock-target" ) );
		repository.flush();
		entityManager.clear();

		DemoPerson locked = entityManager.find(
				DemoPerson.class,
				saved.getId(),
				LockModeType.PESSIMISTIC_WRITE );
		assertNotNull( locked );
		assertEquals( "lock-target", locked.getName() );
		assertEquals(
				LockModeType.PESSIMISTIC_WRITE,
				entityManager.getLockMode( locked ),
				"A-LCK-001: EM must report PESSIMISTIC_WRITE after find-with-lock" );
	}

	@Test
	void pessimisticWriteWithNowaitTimeoutExecutes() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson saved = repository.save( new DemoPerson( "nowait-target" ) );
		repository.flush();
		entityManager.clear();

		// jakarta.persistence.lock.timeout=0 → Dialect appends NOWAIT (A-LCK-003)
		DemoPerson locked = entityManager.find(
				DemoPerson.class,
				saved.getId(),
				LockModeType.PESSIMISTIC_WRITE,
				Map.of( "jakarta.persistence.lock.timeout", 0 ) );
		assertNotNull( locked );
		assertEquals( "nowait-target", locked.getName() );
		assertEquals( LockModeType.PESSIMISTIC_WRITE, entityManager.getLockMode( locked ) );
	}
}
