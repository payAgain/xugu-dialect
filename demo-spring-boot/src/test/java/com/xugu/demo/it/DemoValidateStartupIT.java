package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;
import com.xugu.dialect.XuguDialect;

import jakarta.persistence.EntityManagerFactory;

/**
 * Gated IT: Boot {@code ddl-auto=validate} startup when schema is pre-created (A-SEQ-001).
 * Does <em>not</em> introduce a SEQUENCE entity (Layer B / P-003); exercises validate path
 * with existing {@link DemoPerson} IDENTITY table and dialect sequence-catalog wiring.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false",
		"spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
class DemoValidateStartupIT {

	@BeforeAll
	static void preCreateSchemaForValidate() throws Exception {
		if ( !XuguIntegrationGate.isEnabled() ) {
			return;
		}
		DemoXuguJdbc.ensurePersonTable();
	}

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private Environment environment;

	@Autowired
	private DemoPersonRepository repository;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void validateStartupSucceedsWithPreCreatedSchema() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		String ddlAuto = environment.getProperty( "spring.jpa.hibernate.ddl-auto" );
		assertTrue( "validate".equalsIgnoreCase( ddlAuto ),
				"expected ddl-auto=validate; was " + ddlAuto );

		SessionFactoryImplementor sessionFactory = entityManagerFactory
				.unwrap( SessionFactory.class )
				.unwrap( SessionFactoryImplementor.class );
		var dialect = sessionFactory.getJdbcServices().getDialect();
		assertInstanceOf( XuguDialect.class, dialect );

		String sequencesQuery = dialect.getQuerySequencesString();
		assertNotNull( sequencesQuery, "sequence metadata query must be wired for validate" );
		assertTrue(
				sequencesQuery.toLowerCase( Locale.ROOT ).contains( "all_sequences" ),
				"A-SEQ-001: expected all_sequences catalog query; was: " + sequencesQuery );

		DemoPerson saved = repository.save( new DemoPerson( "validate-ok" ) );
		repository.flush();
		assertNotNull( saved.getId() );
	}
}
