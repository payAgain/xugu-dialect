package com.xugu.dialect;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.it.entities.P002DefaultColumnEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline SchemaExport assert for A-DDL-005: DEFAULT column clause in CREATE DDL.
 */
class XuguDefaultColumnExportTest {

	@Test
	void schemaExportEmitsDefaultColumn_A_DDL_005() throws Exception {
		Path scriptFile = Files.createTempFile( "hib-p002-default-", ".sql" );
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.build();
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P002DefaultColumnEntity.class )
					.buildMetadata();

			Map<String, Object> settings = new HashMap<>();
			settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, Action.NONE );
			settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_ACTION, Action.CREATE_ONLY );
			settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_SCRIPTS_CREATE_TARGET,
					scriptFile.toAbsolutePath().toString() );
			SchemaManagementToolCoordinator.process( metadata, registry, settings, action -> {
			} );

			String ddl = Files.readString( scriptFile, StandardCharsets.UTF_8 );
			String lower = ddl.toLowerCase( Locale.ROOT );
			assertTrue( lower.contains( "create table" ), "expected CREATE TABLE script: " + ddl );
			assertTrue( lower.contains( "default" ), "expected DEFAULT clause in script: " + ddl );
			assertTrue( lower.contains( "active" ), "expected default literal in script: " + ddl );
		}
		finally {
			StandardServiceRegistryBuilder.destroy( registry );
			Files.deleteIfExists( scriptFile );
		}
	}
}
