package com.xugu.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.xugu.demo.entity.DemoPerson;
import com.xugu.demo.repository.DemoPersonRepository;

/**
 * On startup (when {@code xugu.demo.startup-crud=true}): persist + find one {@link DemoPerson}.
 * Disabled in gated IT via {@code application-test} / test property override.
 */
@Component
@ConditionalOnProperty(name = "xugu.demo.startup-crud", havingValue = "true", matchIfMissing = true)
public class DemoStartupCrudRunner implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger( DemoStartupCrudRunner.class );

	private final DemoPersonRepository repository;

	public DemoStartupCrudRunner(DemoPersonRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		DemoPerson saved = repository.save( new DemoPerson( "startup-demo" ) );
		DemoPerson found = repository.findById( saved.getId() )
				.orElseThrow( () -> new IllegalStateException( "persist/find failed for id=" + saved.getId() ) );
		log.info( "Xugu Hibernate demo CRUD OK: {}", found );
	}
}
