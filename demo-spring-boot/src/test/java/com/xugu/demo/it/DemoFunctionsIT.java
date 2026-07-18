package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoJsonDoc;
import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoJsonDocRepository;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: Layer C′ HQL function subset
 * (A-FUN-001/002/004/007/010/016/017). Uses DemoPerson + shared DemoJsonDoc.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false",
		"spring.jpa.properties.hibernate.query.hql.json_functions_enabled=true"
})
@Transactional
class DemoFunctionsIT {

	@Autowired
	private DemoPersonRepository personRepository;

	@Autowired
	private DemoJsonDocRepository jsonDocRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		personRepository.deleteAll();
		jsonDocRepository.deleteAll();
	}

	@Test
	void hqlFunctionSubsetSmoke() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson person = personRepository.save( new DemoPerson( "Alice" ) );
		DemoJsonDoc doc = jsonDocRepository.save( new DemoJsonDoc( "probe", "{\"a\":1,\"s\":\"xugu\"}" ) );
		personRepository.flush();
		jsonDocRepository.flush();
		entityManager.clear();

		Long personId = person.getId();
		Long docId = doc.getId();
		assertNotNull( personId );
		assertNotNull( docId );

		// A-FUN-001 concat
		String concat = entityManager.createQuery(
						"select concat(p.name, '-x') from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", personId )
				.getSingleResult();
		assertEquals( "Alice-x", concat );

		// A-FUN-002 substring
		String sub = entityManager.createQuery(
						"select substring(p.name, 1, 3) from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", personId )
				.getSingleResult();
		assertEquals( "Ali", sub );

		// A-FUN-004 lower / upper
		String lower = entityManager.createQuery(
						"select lower(p.name) from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", personId )
				.getSingleResult();
		assertEquals( "alice", lower );
		String upper = entityManager.createQuery(
						"select upper(p.name) from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", personId )
				.getSingleResult();
		assertEquals( "ALICE", upper );

		// A-FUN-007 coalesce / nullif
		String coalesced = entityManager.createQuery(
						"select coalesce(p.name, 'fallback') from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", personId )
				.getSingleResult();
		assertEquals( "Alice", coalesced );
		String nullif = entityManager.createQuery(
						"select nullif(p.name, 'Alice') from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", personId )
				.getSingleResult();
		assertNull( nullif );

		// A-FUN-010 current_date / current_timestamp
		Object currentDate = entityManager.createQuery( "select current_date", Object.class )
				.getSingleResult();
		assertNotNull( currentDate );
		Object currentTs = entityManager.createQuery( "select current_timestamp", Object.class )
				.getSingleResult();
		assertNotNull( currentTs );

		// A-FUN-016 uuid()
		String uuid = entityManager.createQuery( "select uuid()", String.class ).getSingleResult();
		assertNotNull( uuid );
		assertTrue( uuid.contains( "-" ), "uuid() should be dashed: " + uuid );

		// A-FUN-017 json_value on shared JSON entity payload
		String jv = entityManager.createQuery(
						"select json_value(d.payload, '$.a') from DemoJsonDoc d where d.id = :id",
						String.class )
				.setParameter( "id", docId )
				.getSingleResult();
		assertEquals( "1", jv );
	}
}
