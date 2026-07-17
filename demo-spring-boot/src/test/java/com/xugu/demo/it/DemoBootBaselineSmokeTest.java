package com.xugu.demo.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;
import com.xugu.demo.support.XuguIntegrationGate;
import com.xugu.dialect.XuguDialect;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;

/**
 * Gated live smoke: Spring Boot consumer path — explicit {@link XuguDialect} from
 * {@code application.yml}, {@link SessionFactory} wiring, JPA persist/query, and
 * {@code Pageable} pagination (mirrors dialect {@code XuguHqlPaginationIT} golden path).
 * Default {@code mvn test} skips ({@link XuguIntegrationGate} off).
 */
@SpringBootTest
@EnabledIf( "com.xugu.demo.support.XuguIntegrationGate#isEnabled" )
@TestPropertySource(properties = {
		"xugu.demo.startup-crud=false"
})
@Transactional
class DemoBootBaselineSmokeTest {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Environment environment;

	@Autowired
	private DemoPersonRepository repository;

	@PersistenceContext
	private EntityManager entityManager;

	@AfterEach
	void cleanupRows() {
		repository.deleteAll();
	}

	@Test
	void sessionFactoryUsesExplicitXuguDialectFromApplicationYml() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		String configuredDialect = environment.getProperty(
				"spring.jpa.properties.hibernate.dialect" );
		assertEquals( XuguDialect.class.getName(), configuredDialect );

		SessionFactoryImplementor sessionFactory = entityManagerFactory
				.unwrap( SessionFactory.class )
				.unwrap( SessionFactoryImplementor.class );
		assertInstanceOf( XuguDialect.class, sessionFactory.getJdbcServices().getDialect() );
	}

	@Test
	void datasourceUrlIncludesCompatibleModeNone() throws Exception {
		assertTrue( XuguIntegrationGate.isEnabled() );

		String url = environment.getProperty( "spring.datasource.url" );
		assertTrue( url != null && url.contains( "compatiblemode=NONE" ),
				"Charter JDBC URL must include compatiblemode=NONE" );

		try ( Connection connection = dataSource.getConnection() ) {
			assertTrue( connection.isValid( 5 ), "live XuguDB must accept pooled connection" );
		}
	}

	@Test
	void jpaPersistAndJpqlQueryRoundTrip() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		DemoPerson saved = repository.save( new DemoPerson( "boot-smoke" ) );
		repository.flush();
		entityManager.clear();

		String name = entityManager.createQuery(
				"select p.name from DemoPerson p where p.id = :id", String.class )
				.setParameter( "id", saved.getId() )
				.getSingleResult();
		assertEquals( "boot-smoke", name );
	}

	@Test
	void pageableFindAllUsesLimitOffset() {
		assertTrue( XuguIntegrationGate.isEnabled() );

		repository.save( new DemoPerson( "page-a" ) );
		repository.save( new DemoPerson( "page-b" ) );
		repository.save( new DemoPerson( "page-c" ) );
		repository.flush();
		entityManager.clear();

		Page<DemoPerson> page = repository.findAll(
				PageRequest.of( 0, 2, Sort.by( "id" ).ascending() ) );
		assertEquals( 2, page.getContent().size() );
		assertEquals( 3, page.getTotalElements() );
		assertTrue( page.hasNext() );
	}
}
