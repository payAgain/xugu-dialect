package com.xugu.dialect;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Assert META-INF/services DialectResolver registration is on the classpath
 * and (when a fresh packaged jar exists) inside {@code xugu-dialect-*.jar} (A-SPI-002).
 */
class XuguDialectServicesResourceTest {

	private static final String SERVICES =
			"META-INF/services/org.hibernate.engine.jdbc.dialect.spi.DialectResolver";

	@Test
	void servicesFileOnClasspathListsXuguDialectResolver() throws Exception {
		try ( InputStream in = getClass().getClassLoader().getResourceAsStream( SERVICES ) ) {
			assertNotNull( in, "missing " + SERVICES + " on test classpath" );
			String body = readBody( in );
			assertTrue(
					body.contains( XuguDialectResolver.class.getName() ),
					"services body should list " + XuguDialectResolver.class.getName() + " but was:\n" + body );
		}
	}

	@Test
	void compiledClassesDirectoryContainsServicesFile() throws Exception {
		Path services = Path.of( "target", "classes" ).resolve( SERVICES );
		Assumptions.assumeTrue( Files.isRegularFile( services ), "target/classes not populated" );
		String body = Files.readString( services, StandardCharsets.UTF_8 );
		assertTrue( body.contains( XuguDialectResolver.class.getName() ), body );
	}

	@Test
	void packagedJarContainsServicesEntryWhenFresh() throws Exception {
		Path classesServices = Path.of( "target", "classes" ).resolve( SERVICES );
		Path jar = findDialectJar();
		Assumptions.assumeTrue( jar != null, "no packaged xugu-dialect jar yet (run mvn package)" );
		Assumptions.assumeTrue(
				Files.isRegularFile( classesServices ),
				"target/classes services missing" );
		// Skip stale jars left from earlier package runs before services were added
		Assumptions.assumeTrue(
				Files.getLastModifiedTime( jar ).toMillis()
						>= Files.getLastModifiedTime( classesServices ).toMillis(),
				"packaged jar older than classes services — re-run mvn package" );

		try ( JarFile jf = new JarFile( jar.toFile() ) ) {
			JarEntry entry = jf.getJarEntry( SERVICES );
			assertNotNull( entry, "jar missing " + SERVICES + ": " + jar );
			try ( InputStream in = jf.getInputStream( entry ) ) {
				String body = readBody( in );
				assertTrue(
						body.contains( XuguDialectResolver.class.getName() ),
						"jar services should list resolver; body=\n" + body );
			}
		}
		catch ( Exception e ) {
			fail( "jar services check failed for " + jar + ": " + e.getMessage(), e );
		}
	}

	private static Path findDialectJar() throws Exception {
		Path target = Path.of( "target" );
		if ( !Files.isDirectory( target ) ) {
			return null;
		}
		try ( var stream = Files.list( target ) ) {
			return stream
					.filter( p -> {
						String n = p.getFileName().toString();
						return n.startsWith( "xugu-dialect-" ) && n.endsWith( ".jar" )
								&& !n.contains( "sources" )
								&& !n.contains( "javadoc" )
								&& !n.endsWith( "-tests.jar" );
					} )
					.findFirst()
					.orElse( null );
		}
	}

	private static String readBody(InputStream in) {
		return new BufferedReader( new InputStreamReader( in, StandardCharsets.UTF_8 ) )
				.lines()
				.collect( Collectors.joining( "\n" ) );
	}
}
