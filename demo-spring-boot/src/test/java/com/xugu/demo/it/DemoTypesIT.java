package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoTypedSample;
import com.xugu.demo.repository.DemoTypedSampleRepository;
import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Gated IT: Layer C′ type fields in one round-trip
 * (A-TYP-001/002/004/005/006/008/009/010/012). A-TYP-011 CLOB stays dialect-it-only.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoTypesIT {

	static {
		// Class-load before Spring context: migrate leftover guid-typed column from earlier drafts.
		if ( XuguIntegrationGate.isEnabled() ) {
			try {
				DemoXuguJdbc.recreateTypedSampleTable();
			}
			catch ( SQLException e ) {
				throw new ExceptionInInitializerError( e );
			}
		}
	}

	@Autowired
	private DemoTypedSampleRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void typedSampleRoundTripKeyTypes() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		byte[] bin = new byte[] { 0x01, 0x02, 0x03, 0x04 };
		byte[] blob = new byte[] { 0x0A, 0x0B, 0x0C };
		UUID guid = UUID.fromString( "550e8400-e29b-41d4-a716-446655440000" );
		LocalDate day = LocalDate.of( 2026, 7, 18 );
		LocalDateTime ts = LocalDateTime.of( 2026, 7, 18, 15, 30, 45 );

		DemoTypedSample saved = repository.save( new DemoTypedSample(
				42,
				new BigDecimal( "1234.56" ),
				"hello-xugu",
				true,
				day,
				ts,
				bin,
				blob,
				guid
		) );
		assertNotNull( saved.getId() );
		repository.flush();
		entityManager.clear();

		DemoTypedSample found = repository.findById( saved.getId() )
				.orElseThrow( () -> new AssertionError( "typed sample missing" ) );
		assertEquals( 42, found.getIntVal() );
		assertEquals( 0, new BigDecimal( "1234.56" ).compareTo( found.getDecVal() ) );
		assertEquals( "hello-xugu", found.getLabel() );
		assertEquals( true, found.getFlagVal() );
		assertEquals( day, found.getDayVal() );
		assertEquals( ts, found.getTsVal() );
		assertArrayEquals( bin, found.getBinVal() );
		assertArrayEquals( blob, found.getBlobVal() );
		assertEquals( guid, found.getGuidVal() );
	}
}
