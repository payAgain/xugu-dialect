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

import com.xugu.demo.entity.DemoDept;
import com.xugu.demo.entity.DemoDeptMember;
import com.xugu.demo.repository.DemoDeptMemberRepository;
import com.xugu.demo.repository.DemoDeptRepository;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: association graph (parent + child FK) under Boot (A-SCH-012).
 * UNIQUE column on child is exercised for schema presence (A-SCH-011); violation shape
 * is {@link DemoConstraintRollbackIT}.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoAssociationIT {

	@Autowired
	private DemoDeptRepository deptRepository;

	@Autowired
	private DemoDeptMemberRepository memberRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		memberRepository.deleteAll();
		deptRepository.deleteAll();
	}

	@Test
	void persistDeptWithMembersAndFindViaFk() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoDept dept = new DemoDept( "eng" );
		dept.addMember( "m-alpha", "Alice" );
		dept.addMember( "m-beta", "Bob" );
		DemoDept saved = deptRepository.save( dept );
		deptRepository.flush();
		entityManager.clear();

		DemoDept found = deptRepository.findById( saved.getId() )
				.orElseThrow( () -> new AssertionError( "dept missing after persist" ) );
		assertEquals( "eng", found.getName() );
		assertEquals( 2, found.getMembers().size() );

		DemoDeptMember child = memberRepository.findAll().stream()
				.filter( m -> "m-alpha".equals( m.getCode() ) )
				.findFirst()
				.orElseThrow( () -> new AssertionError( "member m-alpha missing" ) );
		assertNotNull( child.getId() );
		assertEquals( found.getId(), child.getDept().getId() );
		assertEquals( "Alice", child.getName() );
	}
}
