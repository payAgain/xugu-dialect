package com.xugu.dialect;

import java.util.Collections;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.generator.Generator;
import org.hibernate.mapping.GeneratorSettings;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metamodel.mapping.EntityMappingType;
import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.metamodel.spi.RuntimeModelCreationContext;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.query.sqm.mutation.internal.temptable.LocalTemporaryTableInsertStrategy;
import org.hibernate.query.sqm.mutation.spi.SqmMultiTableInsertStrategy;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.spi.TypeConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.it.entities.I003P005BulkDoctor;
import com.xugu.dialect.it.entities.I003P005BulkEngineer;
import com.xugu.dialect.it.entities.I003P005BulkPerson;
import com.xugu.dialect.support.OfflineConnectionProvider;
import com.xugu.dialect.temptable.XuguLocalTemporaryTableStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for C-BULK-001…003 bulk mutation fallback wiring.
 */
class XuguBulkMutationSupportTest {

	private static StandardServiceRegistry registry;
	private static MetadataImplementor bootModel;
	private static SessionFactoryImplementor sessionFactory;
	private static RuntimeModelCreationContext runtimeContext;

	private final XuguDialect dialect = new XuguDialect();

	@BeforeAll
	static void bootJoinedBulkMappingOffline() {
		registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( JdbcSettings.ALLOW_METADATA_ON_BOOT, false )
				.applySetting( AvailableSettings.CONNECTION_PROVIDER, OfflineConnectionProvider.class.getName() )
				.build();
		Metadata metadata = new MetadataSources( registry )
				.addAnnotatedClass( I003P005BulkPerson.class )
				.addAnnotatedClass( I003P005BulkDoctor.class )
				.addAnnotatedClass( I003P005BulkEngineer.class )
				.buildMetadata();
		bootModel = (MetadataImplementor) metadata;
		SessionFactory sf = metadata.buildSessionFactory();
		sessionFactory = (SessionFactoryImplementor) sf;
		runtimeContext = new RuntimeModelCreationContext() {
			@Override
			public SessionFactoryImplementor getSessionFactory() {
				return sessionFactory;
			}

			@Override
			public BootstrapContext getBootstrapContext() {
				return ( (MetadataImpl) bootModel ).getBootstrapContext();
			}

			@Override
			public MetadataImplementor getBootModel() {
				return bootModel;
			}

			@Override
			public MappingMetamodelImplementor getDomainModel() {
				return sessionFactory.getMappingMetamodel();
			}

			@Override
			public TypeConfiguration getTypeConfiguration() {
				return sessionFactory.getTypeConfiguration();
			}

			@Override
			public SqmFunctionRegistry getFunctionRegistry() {
				return sessionFactory.getQueryEngine().getSqmFunctionRegistry();
			}

			@Override
			public Map<String, Object> getSettings() {
				return sessionFactory.getProperties();
			}

			@Override
			public Dialect getDialect() {
				return sessionFactory.getJdbcServices().getDialect();
			}

			@Override
			public CacheImplementor getCache() {
				return sessionFactory.getCache();
			}

			@Override
			public SessionFactoryOptions getSessionFactoryOptions() {
				return sessionFactory.getSessionFactoryOptions();
			}

			@Override
			public JdbcServices getJdbcServices() {
				return sessionFactory.getJdbcServices();
			}

			@Override
			public org.hibernate.boot.model.relational.SqlStringGenerationContext getSqlStringGenerationContext() {
				return sessionFactory.getSqlStringGenerationContext();
			}

			@Override
			public ServiceRegistry getServiceRegistry() {
				return sessionFactory.getServiceRegistry();
			}

			@Override
			public Map<String, Generator> getGenerators() {
				return Collections.emptyMap();
			}

			@Override
			public GeneratorSettings getGeneratorSettings() {
				return null;
			}

			@Override
			public Generator getOrCreateIdGenerator(String entityName, PersistentClass persistentClass) {
				return sessionFactory.getGenerator( entityName );
			}
		};
	}

	@AfterAll
	static void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
		if ( registry != null ) {
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Test
	void supportsSubqueryOnMutatingTableIsFalse_C_BULK_003() {
		assertFalse( dialect.supportsSubqueryOnMutatingTable() );
	}

	@Test
	void localTemporaryTableStrategyForBulkMutation_C_BULK_001() {
		assertEquals( TemporaryTableKind.LOCAL, dialect.getSupportedTemporaryTableKind() );
		assertSame( XuguLocalTemporaryTableStrategy.INSTANCE, dialect.getLocalTemporaryTableStrategy() );
		assertTrue(
				dialect.getTemporaryTableCreateCommand().toLowerCase().contains( "local temporary table" ),
				dialect.getTemporaryTableCreateCommand() );
		assertEquals(
				XuguLocalTemporaryTableStrategy.CREATE_COMMAND,
				dialect.getTemporaryTableCreateCommand() );
	}

	@Test
	void fallbackSqmInsertStrategyWired_C_BULK_002() {
		EntityMappingType rootEntity = sessionFactory.getMappingMetamodel()
				.getEntityDescriptor( I003P005BulkPerson.class );
		assertNotNull( rootEntity, "JOINED root entity mapping must bootstrap offline" );

		RuntimeModelCreationContext context = runtimeContext;
		SqmMultiTableInsertStrategy strategy = dialect.getFallbackSqmInsertStrategy(
				rootEntity,
				context );

		assertNotNull( strategy, "C-BULK-002: dialect must expose insert fallback strategy" );
		assertInstanceOf( LocalTemporaryTableInsertStrategy.class, strategy );
		assertTrue(
				dialect.getTemporaryTableCreateCommand().toLowerCase().contains( "local temporary table" ),
				"insert fallback shares local temp DDL with mutation path" );
	}
}
