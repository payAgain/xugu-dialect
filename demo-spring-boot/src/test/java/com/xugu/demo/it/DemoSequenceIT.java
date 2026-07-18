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

import com.xugu.demo.entity.DemoSeqTicket;
import com.xugu.demo.repository.DemoSeqTicketRepository;
import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: SEQUENCE entity persist + CURRVAL session smoke (A-SEQ-003 / A-SEQ-004).
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoSequenceIT {

	@Autowired
	private DemoSeqTicketRepository ticketRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		ticketRepository.deleteAll();
	}

	@Test
	void persistSequenceTicketGeneratesIncreasingIds() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoSeqTicket a = ticketRepository.save( new DemoSeqTicket( "seq-a" ) );
		DemoSeqTicket b = ticketRepository.save( new DemoSeqTicket( "seq-b" ) );
		ticketRepository.flush();

		assertNotNull( a.getId(), "SEQUENCE id must be assigned" );
		assertNotNull( b.getId(), "SEQUENCE id must be assigned" );
		assertTrue( b.getId() > a.getId(), "sequence ids should increase: " + a.getId() + " then " + b.getId() );
	}

	@Test
	void currvalMatchesLastGeneratedIdInSession() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoSeqTicket saved = ticketRepository.save( new DemoSeqTicket( "currval-probe" ) );
		ticketRepository.flush();
		assertNotNull( saved.getId() );

		Number currval = (Number) entityManager.createNativeQuery(
				"SELECT CURRVAL('" + DemoXuguJdbc.SEQ_TICKET_SEQUENCE + "') FROM DUAL" )
				.getSingleResult();
		assertEquals(
				saved.getId().longValue(),
				currval.longValue(),
				"A-SEQ-004: CURRVAL after NEXTVAL in same session must match assigned id" );
	}
}
