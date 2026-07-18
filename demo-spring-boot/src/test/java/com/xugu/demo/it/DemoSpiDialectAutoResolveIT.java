package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.XuguIntegrationGate;
import com.xugu.dialect.XuguDialect;

import jakarta.persistence.EntityManagerFactory;

/**
 * Gated IT: Boot SPI dialect resolve without explicit {@code hibernate.dialect}
 * (A-SPI-002 / A-SPI-003). Empty property overrides {@code application.yml}.
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false",
		// Clear explicit dialect so DialectResolver SPI selects XuguDialect
		"spring.jpa.properties.hibernate.dialect=",
		"spring.jpa.database-platform="
})
@Transactional
class DemoSpiDialectAutoResolveIT {

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
	void sessionFactoryResolvesXuguDialectWithoutExplicitConfig() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		String configuredDialect = environment.getProperty(
				"spring.jpa.properties.hibernate.dialect" );
		assertTrue(
				configuredDialect == null || configuredDialect.isBlank(),
				"explicit hibernate.dialect must be unset for SPI path; was: " + configuredDialect );

		SessionFactoryImplementor sessionFactory = entityManagerFactory
				.unwrap( SessionFactory.class )
				.unwrap( SessionFactoryImplementor.class );
		var dialect = sessionFactory.getJdbcServices().getDialect();
		assertInstanceOf( XuguDialect.class, dialect,
				"SPI should resolve XuguDialect; got " + dialect.getClass().getName() );
		assertTrue( dialect.getVersion().getMajor() >= 12,
				"A-SPI-003: dialect version from live metadata; major=" + dialect.getVersion().getMajor() );

		DemoPerson saved = repository.save( new DemoPerson( "spi-boot" ) );
		repository.flush();
		assertTrue( saved.getId() != null && saved.getId() > 0 );
	}
}
