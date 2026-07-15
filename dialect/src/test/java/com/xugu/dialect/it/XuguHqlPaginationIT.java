package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.query.Query;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.P001HqlPageEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import jakarta.persistence.LockModeType;
import jakarta.persistence.Timeout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Gated IT: HQL setFirstResult/setMaxResults via SqlAstTranslator (I-002 P-001).
 * Asserts generated SQL uses LIMIT (not ANSI FETCH FIRST / ROWS ONLY) and window is correct.
 * Also proves AST lock+page order: FOR UPDATE before LIMIT (WAIT after LIMIT when present).
 */
class XuguHqlPaginationIT {

	private static final String TABLE = "HIB_P001_HQL_PAGE";

	@Test
	void hqlSetFirstResultMaxResultsUsesLimitNotFetchFirst() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		assertNotNull( new XuguDialect().getSqlAstTranslatorFactory(),
				"factory must be wired before SessionFactory boot" );

		cleanup();

		CapturingInspector inspector = new CapturingInspector();
		StandardServiceRegistry registry = buildRegistry( inspector );
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P001HqlPageEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );

			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				for ( int i = 1; i <= 10; i++ ) {
					session.persist( new P001HqlPageEntity( i, "r" + i ) );
				}
				session.getTransaction().commit();
			}

			inspector.clear();

			List<Integer> page;
			try ( Session session = sf.openSession() ) {
				page = session.createQuery(
								"select e.id from P001HqlPageEntity e order by e.id",
								Integer.class )
						.setFirstResult( 4 )
						.setMaxResults( 3 )
						.getResultList();
			}

			assertEquals( List.of( 5, 6, 7 ), page, "HQL pagination window must be ids 5,6,7" );

			List<String> selectSql = inspector.selectSqlContaining( TABLE );
			assertFalse( selectSql.isEmpty(), "StatementInspector must capture HQL select SQL" );

			for ( String sql : selectSql ) {
				String lower = sql.toLowerCase( Locale.ROOT );
				assertFalse( lower.contains( "fetch first" ), "must not emit FETCH FIRST: " + sql );
				assertFalse( lower.contains( "rows only" ), "must not emit ROWS ONLY: " + sql );
				assertTrue( lower.contains( "limit" ), "must use LIMIT: " + sql );
				assertTrue( lower.contains( "offset" ), "must use OFFSET form: " + sql );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			fail( "HQL pagination IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	/**
	 * MAJOR fix (rev-p001-20260715): prove AST/HQL path emits XuGu order
	 * {@code FOR UPDATE … LIMIT ? [OFFSET ?]} and
	 * {@code FOR UPDATE … LIMIT ? [OFFSET ?] WAIT ms}, matching {@code XuguLimitHandler}.
	 */
	@Test
	void hqlLockAndPageEmitsForUpdateBeforeLimitAndWaitAfter() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );

		cleanup();

		CapturingInspector inspector = new CapturingInspector();
		StandardServiceRegistry registry = buildRegistry( inspector );
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( P001HqlPageEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );

			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				for ( int i = 1; i <= 5; i++ ) {
					session.persist( new P001HqlPageEntity( i, "r" + i ) );
				}
				session.getTransaction().commit();
			}

			inspector.clear();

			List<Integer> lockedPage;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				lockedPage = session.createQuery(
								"select e.id from P001HqlPageEntity e order by e.id",
								Integer.class )
						.setFirstResult( 1 )
						.setMaxResults( 2 )
						.setLockMode( LockModeType.PESSIMISTIC_WRITE )
						.getResultList();
				session.getTransaction().commit();
			}

			assertEquals( List.of( 2, 3 ), lockedPage, "locked HQL page window must be ids 2,3" );

			List<String> fuSql = inspector.selectSqlContaining( TABLE );
			assertFalse( fuSql.isEmpty(), "must capture locked HQL select SQL" );
			String observedFu = null;
			for ( String sql : fuSql ) {
				String lower = sql.toLowerCase( Locale.ROOT );
				if ( !lower.contains( "for update" ) || !lower.contains( "limit" ) ) {
					continue;
				}
				observedFu = sql;
				assertFalse( lower.contains( "fetch first" ), "must not emit FETCH FIRST: " + sql );
				int fuAt = lower.indexOf( "for update" );
				int limAt = lower.indexOf( "limit" );
				assertTrue( fuAt >= 0 && limAt >= 0 && fuAt < limAt,
						"expected FOR UPDATE before LIMIT (AST/HQL): " + sql );
			}
			assertNotNull( observedFu, "expected at least one FOR UPDATE…LIMIT HQL SQL; captured=" + fuSql );

			inspector.clear();

			List<Integer> waitPage;
			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				Query<Integer> q = session.createQuery(
						"select e.id from P001HqlPageEntity e order by e.id",
						Integer.class );
				waitPage = q.setFirstResult( 0 )
						.setMaxResults( 2 )
						.setLockMode( LockModeType.PESSIMISTIC_WRITE )
						.setTimeout( Timeout.milliseconds( 2000 ) )
						.getResultList();
				session.getTransaction().commit();
			}

			assertEquals( List.of( 1, 2 ), waitPage, "WAIT locked HQL page window must be ids 1,2" );

			List<String> waitSqls = inspector.selectSqlContaining( TABLE );
			assertFalse( waitSqls.isEmpty(), "must capture WAIT locked HQL select SQL" );
			String observedWait = null;
			for ( String sql : waitSqls ) {
				String lower = sql.toLowerCase( Locale.ROOT );
				if ( !lower.contains( "for update" ) || !lower.contains( "limit" ) || !lower.contains( "wait" ) ) {
					continue;
				}
				observedWait = sql;
				int fuAt = lower.indexOf( "for update" );
				int limAt = lower.indexOf( "limit" );
				int waitAt = lower.lastIndexOf( "wait" );
				assertTrue( fuAt < limAt && limAt < waitAt,
						"expected FOR UPDATE … LIMIT … WAIT (AST/HQL): " + sql );
			}
			assertNotNull( observedWait,
					"expected FOR UPDATE…LIMIT…WAIT HQL SQL; captured=" + waitSqls );

			export( metadata, registry, Action.DROP );
		}
		catch ( Exception e ) {
			fail( "HQL lock+page IT failed: " + e.getMessage(), e );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
			cleanup();
		}
	}

	private static StandardServiceRegistry buildRegistry(StatementInspector inspector) {
		return new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.JAKARTA_JDBC_DRIVER, XuguTestConnection.DRIVER )
				.applySetting( JdbcSettings.JAKARTA_JDBC_URL, XuguTestConnection.jdbcUrl() )
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.applySetting( SchemaToolingSettings.HBM2DDL_AUTO, "none" )
				.applySetting( JdbcSettings.SHOW_SQL, "true" )
				.applySetting( JdbcSettings.STATEMENT_INSPECTOR, inspector )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, a -> {
		} );
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			try {
				st.execute( "DROP TABLE IF EXISTS " + TABLE );
			}
			catch ( Exception ignored ) {
			}
		}
		catch ( Exception ignored ) {
		}
	}

	private static final class CapturingInspector implements StatementInspector {
		private final List<String> sqls = new CopyOnWriteArrayList<>();

		@Override
		public String inspect(String sql) {
			if ( sql != null ) {
				sqls.add( sql );
			}
			return sql;
		}

		void clear() {
			sqls.clear();
		}

		List<String> selectSqlContaining(String token) {
			List<String> out = new ArrayList<>();
			String needle = token.toLowerCase( Locale.ROOT );
			for ( String sql : sqls ) {
				String lower = sql.toLowerCase( Locale.ROOT );
				if ( lower.contains( "select" ) && lower.contains( needle ) ) {
					out.add( sql );
				}
			}
			return out;
		}
	}
}
