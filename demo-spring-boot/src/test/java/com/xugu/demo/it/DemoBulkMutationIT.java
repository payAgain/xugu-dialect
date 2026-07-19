package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * Gated IT: HQL bulk update + bulk delete via Boot EM (C-BULK-001 deepening, B-DEMO-001).
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoBulkMutationIT {

	@Autowired
	private DemoPersonRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void bulkUpdatePersonNames() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		repository.save( new DemoPerson( "bulk-a" ) );
		repository.save( new DemoPerson( "bulk-b" ) );
		repository.save( new DemoPerson( "keep" ) );
		repository.flush();
		entityManager.clear();

		int updated = entityManager.createQuery(
						"update DemoPerson p set p.name = :neu where p.name like :pat" )
				.setParameter( "neu", "bulk-updated" )
				.setParameter( "pat", "bulk-%" )
				.executeUpdate();
		assertEquals( 2, updated, "C-BULK-001: bulk update row count" );
		entityManager.clear();

		long updatedCount = repository.findAll().stream()
				.filter( p -> "bulk-updated".equals( p.getName() ) )
				.count();
		assertEquals( 2L, updatedCount );
		assertEquals( 1L, repository.findAll().stream()
				.filter( p -> "keep".equals( p.getName() ) )
				.count() );
	}

	@Test
	void bulkDeletePersonNames() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		repository.save( new DemoPerson( "del-a" ) );
		repository.save( new DemoPerson( "del-b" ) );
		repository.save( new DemoPerson( "keep-del" ) );
		repository.flush();
		entityManager.clear();

		int deleted = entityManager.createQuery(
						"delete from DemoPerson p where p.name like :pat" )
				.setParameter( "pat", "del-%" )
				.executeUpdate();
		assertEquals( 2, deleted, "B-DEMO-001: bulk delete row count" );
		entityManager.clear();

		assertEquals( 1L, repository.count() );
		assertEquals( 1L, repository.findAll().stream()
				.filter( p -> "keep-del".equals( p.getName() ) )
				.count() );
	}
}
