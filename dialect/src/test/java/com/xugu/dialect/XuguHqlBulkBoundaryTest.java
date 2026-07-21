package com.xugu.dialect;

import java.util.Locale;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SyntaxException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.it.entities.I003P005BulkDoctor;
import com.xugu.dialect.it.entities.I003P005BulkEngineer;
import com.xugu.dialect.it.entities.I003P005BulkPerson;
import com.xugu.dialect.it.entities.I010P013StAnimal;
import com.xugu.dialect.it.entities.I010P013StCat;
import com.xugu.dialect.it.entities.I010P013StDog;
import com.xugu.dialect.support.OfflineConnectionProvider;
import com.xugu.dialect.temptable.XuguLocalTemporaryTableStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline Unit for XP-003 / P-013: HQL bulk support/reject boundary matrix.
 *
 * <p>Matrix (Hibernate mapping — not EF Owned):
 * <ul>
 *   <li><b>supported (flags)</b>: local temp-table mutation/insert fallback wiring;
 *       {@code supportsSubqueryOnMutatingTable == false}</li>
 *   <li><b>rejected (create-time)</b>: HQL update/delete with {@code order by} or
 *       inline {@code limit} — Hibernate grammar rejects at parse ({@link SyntaxException})</li>
 *   <li><b>API reject</b>: {@link MutationQuery} has no {@code setMaxResults} (slim mutation API)</li>
 *   <li><b>soft / not applied</b>: {@code session.createQuery(updateHql).setMaxResults(n)} is a
 *       silent no-op ({@code MutationQueryOptions.LimitImpl}); live IT nails row counts</li>
 * </ul>
 *
 * @see com.xugu.dialect.it.XuguHqlBulkBoundaryIT
 * @see com.xugu.dialect.XuguBulkMutationSupportTest
 */
class XuguHqlBulkBoundaryTest {

	private static StandardServiceRegistry registry;
	private static SessionFactory sessionFactory;

	private final XuguDialect dialect = new XuguDialect();

	@BeforeAll
	static void bootOffline() {
		registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( JdbcSettings.ALLOW_METADATA_ON_BOOT, false )
				.applySetting( AvailableSettings.CONNECTION_PROVIDER, OfflineConnectionProvider.class.getName() )
				.build();
		Metadata metadata = new MetadataSources( registry )
				.addAnnotatedClass( I003P005BulkPerson.class )
				.addAnnotatedClass( I003P005BulkDoctor.class )
				.addAnnotatedClass( I003P005BulkEngineer.class )
				.addAnnotatedClass( I010P013StAnimal.class )
				.addAnnotatedClass( I010P013StDog.class )
				.addAnnotatedClass( I010P013StCat.class )
				.buildMetadata();
		sessionFactory = metadata.buildSessionFactory();
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
	void dialectBulkTempTableFlags_XP003() {
		assertFalse( dialect.supportsSubqueryOnMutatingTable(),
				"Xugu forces multi-table mutation via local temp fallback" );
		assertEquals( TemporaryTableKind.LOCAL, dialect.getSupportedTemporaryTableKind() );
		assertSame( XuguLocalTemporaryTableStrategy.INSTANCE, dialect.getLocalTemporaryTableStrategy() );
		assertTrue(
				dialect.getTemporaryTableCreateCommand().toLowerCase( Locale.ROOT )
						.contains( "local temporary table" ),
				dialect.getTemporaryTableCreateCommand() );
		assertEquals(
				XuguLocalTemporaryTableStrategy.CREATE_COMMAND,
				dialect.getTemporaryTableCreateCommand() );
	}

	@Test
	void joinedAndSingleTableMappingsBootstrapOffline_XP003() {
		assertTrue( sessionFactory.getMetamodel().entity( I003P005BulkPerson.class ) != null );
		assertTrue( sessionFactory.getMetamodel().entity( I010P013StAnimal.class ) != null );
		assertTrue( sessionFactory.getMetamodel().entity( I010P013StDog.class ) != null );
		assertTrue( sessionFactory.getMetamodel().entity( I010P013StCat.class ) != null );
	}

	@Test
	void mutationHqlWithOrderByRejectedAtCreate_XP003() {
		try ( Session session = sessionFactory.openSession() ) {
			RuntimeException updateEx = assertThrows(
					RuntimeException.class,
					() -> session.createMutationQuery(
							"update I003P005BulkPerson set name = 'x' order by id" ),
					"HQL update + order by must fail at create/parse" );
			assertInstanceOf( SyntaxException.class, rootCause( updateEx ),
					() -> "expected SyntaxException, got: " + describe( updateEx ) );

			RuntimeException deleteEx = assertThrows(
					RuntimeException.class,
					() -> session.createMutationQuery(
							"delete from I010P013StCat order by id" ),
					"HQL delete + order by must fail at create/parse" );
			assertInstanceOf( SyntaxException.class, rootCause( deleteEx ),
					() -> "expected SyntaxException, got: " + describe( deleteEx ) );
		}
	}

	@Test
	void mutationHqlWithInlineLimitRejectedAtCreate_XP003() {
		try ( Session session = sessionFactory.openSession() ) {
			RuntimeException updateEx = assertThrows(
					RuntimeException.class,
					() -> session.createMutationQuery(
							"update I010P013StDog set name = 'x' where name is not null limit 1" ),
					"HQL update + limit must fail at create/parse" );
			assertInstanceOf( SyntaxException.class, rootCause( updateEx ),
					() -> "expected SyntaxException, got: " + describe( updateEx ) );

			RuntimeException deleteEx = assertThrows(
					RuntimeException.class,
					() -> session.createMutationQuery(
							"delete from I003P005BulkDoctor where employed = true limit 1" ),
					"HQL delete + limit must fail at create/parse" );
			assertInstanceOf( SyntaxException.class, rootCause( deleteEx ),
					() -> "expected SyntaxException, got: " + describe( deleteEx ) );
		}
	}

	/**
	 * Soft boundary: Hibernate 7.4 {@link MutationQuery} does not expose {@code setMaxResults};
	 * limit-on-mutation is enforced via HQL parse reject (see {@code XuguHqlBulkBoundaryIT}),
	 * not a SelectionQuery-style soft no-op.
	 */
	@Test
	void mutationQueryHasNoSetMaxResultsApi_XP003() {
		try ( Session session = sessionFactory.openSession() ) {
			MutationQuery query = session.createMutationQuery(
					"update I003P005BulkPerson set name = :name where employed = :employed" );
			query.setParameter( "name", "soft" );
			query.setParameter( "employed", true );
			boolean hasSetMaxResults = false;
			for ( java.lang.reflect.Method m : MutationQuery.class.getMethods() ) {
				if ( "setMaxResults".equals( m.getName() ) ) {
					hasSetMaxResults = true;
					break;
				}
			}
			assertTrue( !hasSetMaxResults,
					"MutationQuery must not expose setMaxResults in 7.4.5.Final (soft API absent)" );
		}
	}

	private static Throwable rootCause(Throwable t) {
		Throwable cur = t;
		while ( cur.getCause() != null && cur.getCause() != cur ) {
			cur = cur.getCause();
		}
		return cur;
	}

	private static String describe(Throwable t) {
		StringBuilder sb = new StringBuilder();
		Throwable cur = t;
		while ( cur != null ) {
			if ( sb.length() > 0 ) {
				sb.append( " <- " );
			}
			sb.append( cur.getClass().getName() ).append( ": " ).append( cur.getMessage() );
			Throwable next = cur.getCause();
			if ( next == cur ) {
				break;
			}
			cur = next;
		}
		return sb.toString();
	}
}
