package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.DemoXuguJdbc;
import com.xugu.demo.support.XuguIntegrationGate;

/**
 * Gated IT: Boot {@code ddl-auto=update} consumer schema surface (A-DDL-001).
 * Proves {@code HIB_DEMO_*} table exists after Spring Boot / Hibernate schema tooling.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false",
		"spring.jpa.hibernate.ddl-auto=update"
})
@Transactional
class DemoSchemaSurfaceIT {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment environment;

	@Autowired
	private DemoPersonRepository repository;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void hibDemoPersonTableExistsAfterUpdateStartup() throws Exception {
		assertTrue( XuguIntegrationGate.isEnabled() );

		String ddlAuto = environment.getProperty( "spring.jpa.hibernate.ddl-auto" );
		assertTrue( "update".equalsIgnoreCase( ddlAuto ), "expected ddl-auto=update; was " + ddlAuto );

		try ( Connection connection = dataSource.getConnection();
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT 1 FROM " + DemoXuguJdbc.PERSON_TABLE + " WHERE 1=0" ) ) {
			assertTrue( rs != null, DemoXuguJdbc.PERSON_TABLE + " must be queryable after update path" );
		}
	}
}
