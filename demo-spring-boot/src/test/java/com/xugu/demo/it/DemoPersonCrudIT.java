package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
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
 * Gated IT: Spring Boot + JPA persist/find against real XuguDB.
 * Default {@code mvn test} skips (gate off). Enable with
 * {@code -Dxugu.run.integration=true} or env {@code XUGU_RUN_IT=true}.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoPersonCrudIT {

	private static final String TABLE = "HIB_DEMO_PERSON";

	@Autowired
	private DemoPersonRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@AfterAll
	static void dropDemoTable() throws Exception {
		// Resolve the same env/defaults as application.yml without Spring context.
		String url = System.getenv( "XUGU_JDBC_URL" );
		if ( url == null || url.isBlank() ) {
			url = "jdbc:xugu://127.0.0.1:5138/SYSTEM?compatiblemode=NONE";
		}
		String user = System.getenv( "XUGU_USER" );
		if ( user == null || user.isBlank() ) {
			user = "SYSDBA";
		}
		String password = System.getenv( "XUGU_PASSWORD" );
		if ( password == null || password.isBlank() ) {
			password = "SYSDBA";
		}
		Class.forName( "com.xugu.cloudjdbc.Driver" );
		try ( Connection c = DriverManager.getConnection( url, user, password );
				Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
		}
	}

	@Test
	void persistAndFindPerson() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson saved = repository.save( new DemoPerson( "it-person" ) );
		assertNotNull( saved.getId(), "IDENTITY id must be backfilled" );
		assertTrue( saved.getId() > 0 );

		repository.flush();
		entityManager.clear();

		DemoPerson found = repository.findById( saved.getId() )
				.orElseThrow( () -> new AssertionError( "findById returned empty" ) );
		assertEquals( "it-person", found.getName() );
		assertEquals( saved.getId(), found.getId() );
	}
}
