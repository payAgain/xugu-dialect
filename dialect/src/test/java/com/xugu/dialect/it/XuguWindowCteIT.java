package com.xugu.dialect.it;

import java.sql.Connection;
import java.sql.Statement;
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
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.XuguDialect;
import com.xugu.dialect.it.entities.I003P004WinEntity;
import com.xugu.dialect.support.XuguITGate;
import com.xugu.dialect.support.XuguTestConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ORM entrypoint IT for C-WIN-001 / C-CTE-001: HQL window {@code OVER} and {@code WITH} CTE.
 */
class XuguWindowCteIT {

	private static final String TABLE = "HIB_I003_P004_WIN";

	@Test
	void hqlWindowAndWithClauseOnLiveSession() {
		Assumptions.assumeTrue( XuguITGate.isEnabled(), "integration gate off" );
		assertTrue( new XuguDialect().supportsWindowFunctions() );
		assertTrue( new XuguDialect().supportsWithClause() );

		cleanup();

		CapturingInspector inspector = new CapturingInspector();
		StandardServiceRegistry registry = buildRegistry( inspector );
		SessionFactory sf = null;
		try {
			Metadata metadata = new MetadataSources( registry )
					.addAnnotatedClass( I003P004WinEntity.class )
					.buildMetadata();
			export( metadata, registry, Action.CREATE_ONLY );
			sf = metadata.buildSessionFactory();

			try ( Session session = sf.openSession() ) {
				session.beginTransaction();
				session.persist( new I003P004WinEntity( 1, "g1", 10 ) );
				session.persist( new I003P004WinEntity( 2, "g1", 30 ) );
				session.persist( new I003P004WinEntity( 3, "g2", 20 ) );
				session.getTransaction().commit();
			}

			inspector.clear();
			try ( Session session = sf.openSession() ) {
				List<Object[]> ranked = session.createQuery(
						"select e.id, row_number() over (partition by e.grpKey order by e.score desc) "
								+ "from I003P004WinEntity e order by e.id",
						Object[].class )
						.getResultList();
				assertEquals( 3, ranked.size() );
				// id=2 has highest score in g1 → row_number 1
				Object[] row2 = ranked.stream().filter( r -> Integer.valueOf( 2 ).equals( r[0] ) ).findFirst()
						.orElseThrow();
				assertEquals( 1L, ( (Number) row2[1] ).longValue() );
			}

			List<String> windowSql = inspector.sqlContaining( "over" );
			assertFalse( windowSql.isEmpty(), "expected OVER in generated SQL" );
			for ( String sql : windowSql ) {
				assertTrue( sql.toLowerCase( Locale.ROOT ).contains( "over" ), sql );
			}

			inspector.clear();
			try ( Session session = sf.openSession() ) {
				List<Integer> ids = session.createQuery(
						"with cte as (select e.id as id, e.score as score from I003P004WinEntity e where e.grpKey = 'g1') "
								+ "select c.id from cte c order by c.score desc",
						Integer.class )
						.getResultList();
				assertEquals( List.of( 2, 1 ), ids );
			}

			List<String> cteSql = inspector.sqlContaining( "with" );
			assertFalse( cteSql.isEmpty(), "expected WITH in generated SQL" );
			for ( String sql : cteSql ) {
				assertTrue( sql.toLowerCase( Locale.ROOT ).contains( "with" ), sql );
			}

			export( metadata, registry, Action.DROP );
		}
		catch ( AssertionError e ) {
			throw e;
		}
		catch ( Exception e ) {
			fail( "Window/CTE IT failed: " + e.getMessage(), e );
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
				.applySetting( JdbcSettings.STATEMENT_INSPECTOR, inspector )
				.build();
	}

	private static void export(Metadata metadata, StandardServiceRegistry registry, Action action) {
		Map<String, Object> settings = new HashMap<>();
		settings.put( SchemaToolingSettings.JAKARTA_HBM2DDL_DATABASE_ACTION, action );
		SchemaManagementToolCoordinator.process( metadata, registry, settings, completion -> {
		} );
	}

	private static void cleanup() {
		try ( Connection c = XuguTestConnection.open(); Statement st = c.createStatement() ) {
			st.execute( "DROP TABLE IF EXISTS " + TABLE );
		}
		catch ( Exception ignored ) {
		}
	}

	private static final class CapturingInspector implements StatementInspector {
		private final CopyOnWriteArrayList<String> sqls = new CopyOnWriteArrayList<>();

		@Override
		public String inspect(String sql) {
			sqls.add( sql );
			return sql;
		}

		void clear() {
			sqls.clear();
		}

		List<String> sqlContaining(String token) {
			String t = token.toLowerCase( Locale.ROOT );
			return sqls.stream().filter( s -> s.toLowerCase( Locale.ROOT ).contains( t ) ).toList();
		}
	}
}
