package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.xugu.demo.entity.DemoDept;
import com.xugu.demo.entity.DemoDeptMember;
import com.xugu.demo.repository.DemoDeptMemberRepository;
import com.xugu.demo.repository.DemoDeptRepository;
import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: UNIQUE → ConstraintViolationException (C-EXC-001), optional NOT NULL extract
 * (C-EXC-002), and observable transaction rollback (A-XCUT-004).
 * <p>No class-level {@code @Transactional} — scenarios need real commit boundaries.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
class DemoConstraintRollbackIT {

	@Autowired
	private DemoDeptRepository deptRepository;

	@Autowired
	private DemoDeptMemberRepository memberRepository;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		TransactionTemplate tx = new TransactionTemplate( transactionManager );
		tx.executeWithoutResult( status -> {
			memberRepository.deleteAll();
			deptRepository.deleteAll();
		} );
	}

	@Test
	void uniqueViolationMapsToConstraintViolationException() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		TransactionTemplate tx = new TransactionTemplate( transactionManager );
		Long deptId = tx.execute( status -> {
			DemoDept dept = deptRepository.save( new DemoDept( "unique-dept" ) );
			memberRepository.save( new DemoDeptMember( "DUP-CODE", "first", dept ) );
			memberRepository.flush();
			return dept.getId();
		} );
		assertNotNull( deptId );

		ConstraintViolationException cve = null;
		try {
			tx.executeWithoutResult( status -> {
				DemoDept dept = deptRepository.findById( deptId )
						.orElseThrow( () -> new AssertionError( "dept missing" ) );
				memberRepository.save( new DemoDeptMember( "DUP-CODE", "second", dept ) );
				memberRepository.flush();
			} );
			fail( "expected unique constraint violation on duplicate code" );
		}
		catch ( RuntimeException ex ) {
			cve = findConstraintViolation( ex );
			if ( cve == null ) {
				fail( "expected ConstraintViolationException in cause chain, got: " + ex, ex );
			}
		}

		assertNotNull( cve );
		assertEquals( ConstraintKind.UNIQUE, cve.getKind(), "C-EXC-001: UNIQUE kind" );
	}

	@Test
	void forcedFailureRollsBackDurableMemberRow() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		TransactionTemplate tx = new TransactionTemplate( transactionManager );
		Long deptId = tx.execute( status -> {
			DemoDept dept = deptRepository.save( new DemoDept( "rollback-dept" ) );
			return dept.getId();
		} );

		long before = countMembers();

		try {
			tx.executeWithoutResult( status -> {
				DemoDept dept = deptRepository.findById( deptId )
						.orElseThrow( () -> new AssertionError( "dept missing" ) );
				memberRepository.save( new DemoDeptMember( "rb-only", "will-rollback", dept ) );
				memberRepository.flush();
				throw new RuntimeException( "force-rollback" );
			} );
			fail( "expected force-rollback" );
		}
		catch ( RuntimeException ex ) {
			assertTrue(
					ex.getMessage() != null && ex.getMessage().contains( "force-rollback" ),
					"unexpected failure: " + ex );
		}

		assertEquals( before, countMembers(), "A-XCUT-004: rolled-back member must not be durable" );
	}

	/**
	 * Stretch / optional C-EXC-002: NOT NULL via native UPDATE bypasses Hibernate pre-check.
	 */
	@Test
	void notNullViolationExtractsConstraintNameWhenPresent() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		TransactionTemplate tx = new TransactionTemplate( transactionManager );
		Long memberId = tx.execute( status -> {
			DemoDept dept = deptRepository.save( new DemoDept( "nn-dept" ) );
			DemoDeptMember member = memberRepository.save(
					new DemoDeptMember( "nn-code", "valid-name", dept ) );
			memberRepository.flush();
			return member.getId();
		} );

		ConstraintViolationException cve = null;
		try {
			tx.executeWithoutResult( status -> {
				entityManager.createNativeQuery(
								"UPDATE " + DemoXuguJdbc.DEPT_MEMBER_TABLE
										+ " SET name = NULL WHERE id = :id" )
						.setParameter( "id", memberId )
						.executeUpdate();
				entityManager.flush();
			} );
			fail( "expected NOT NULL constraint violation" );
		}
		catch ( RuntimeException ex ) {
			cve = findConstraintViolation( ex );
			if ( cve == null ) {
				fail( "expected ConstraintViolationException in cause chain, got: " + ex, ex );
			}
		}

		assertNotNull( cve );
		assertEquals( ConstraintKind.NOT_NULL, cve.getKind(), "C-EXC-002: NOT_NULL kind" );
		if ( cve.getConstraintName() != null ) {
			assertTrue(
					cve.getConstraintName().equalsIgnoreCase( "name" ),
					"extractor should yield violated column name, got: " + cve.getConstraintName() );
		}
	}

	private long countMembers() {
		TransactionTemplate tx = new TransactionTemplate( transactionManager );
		Long count = tx.execute( status -> memberRepository.count() );
		return count == null ? 0L : count;
	}

	private static ConstraintViolationException findConstraintViolation(Throwable ex) {
		Throwable t = ex;
		while ( t != null ) {
			if ( t instanceof ConstraintViolationException found ) {
				return found;
			}
			t = t.getCause();
		}
		return null;
	}
}
