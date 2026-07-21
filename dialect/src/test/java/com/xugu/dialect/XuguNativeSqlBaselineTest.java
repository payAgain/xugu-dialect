package com.xugu.dialect;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Types;
import java.util.Locale;

import org.hibernate.Timeouts;
import org.hibernate.query.spi.Limit;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.identity.XuguIdentityColumnSupport;
import com.xugu.dialect.pagination.XuguLimitHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline SQL goldens (XP-006 / P-015): LIMIT/OFFSET, FOR UPDATE+LIMIT order, IDENTITY DDL.
 * Baselines under {@code classpath:sql-baselines/}; whitespace-normalized Equal.
 */
class XuguNativeSqlBaselineTest {

	private static final String BASELINE_DIR = "sql-baselines/";

	private final XuguDialect dialect = new XuguDialect();
	private final XuguLimitHandler limits = XuguLimitHandler.INSTANCE;

	@Test
	void limitOffsetMatchesBaseline() {
		Limit limit = new Limit( 5, 10 );
		String sql = limits.processSql( "select id from t order by id", limit );
		assertEqualsBaseline( "limit-offset.sql", sql );
		assertFalse( sql.toLowerCase( Locale.ROOT ).contains( "fetch first" ) );
		assertFalse( sql.contains( "limit ?,?" ) );
	}

	@Test
	void forUpdateLimitOrderMatchesBaseline() {
		String locked = "select id from t order by id" + dialect.getForUpdateString();
		Limit limit = new Limit( null, 3 );
		String sql = limits.processSql( locked, limit );
		assertEqualsBaseline( "for-update-limit-order.sql", sql );

		String lower = sql.toLowerCase( Locale.ROOT );
		int fuAt = lower.indexOf( "for update" );
		int limAt = lower.indexOf( "limit" );
		assertTrue( fuAt >= 0 && limAt > fuAt, "XuGu order: FOR UPDATE before LIMIT: " + sql );
	}

	@Test
	void identityColumnFragmentMatchesBaseline() {
		String fragment = dialect.getIdentityColumnSupport().getIdentityColumnString( Types.INTEGER );
		assertEquals( XuguIdentityColumnSupport.INSTANCE.getIdentityColumnString( Types.INTEGER ), fragment );
		assertEqualsBaseline( "identity-column.fragment.sql", fragment );
		assertFalse( fragment.toLowerCase( Locale.ROOT ).contains( "auto_increment" ),
				"prefer IDENTITY in NONE mode" );
	}

	@Test
	void baselinesRejectSkipLockedAndForShareAsPositive() {
		for ( String name : new String[] {
				"limit-offset.sql",
				"for-update-limit-order.sql",
				"identity-column.fragment.sql"
		} ) {
			String baseline = loadBaseline( name );
			String lower = baseline.toLowerCase( Locale.ROOT );
			assertFalse( lower.contains( "skip locked" ), "baseline must not gold SKIP LOCKED: " + name );
			assertFalse( lower.contains( "for share" ), "baseline must not gold FOR SHARE: " + name );
		}
		assertFalse( dialect.supportsSkipLocked() );
		assertFalse( dialect.getReadLockString( Timeouts.WAIT_FOREVER ).toLowerCase( Locale.ROOT ).contains( "share" ) );
	}

	private static void assertEqualsBaseline(String relativeName, String actualSql) {
		assertEquals( normalize( loadBaseline( relativeName ) ), normalize( actualSql ),
				() -> "SQL must Equal baseline " + relativeName + " (normalized whitespace)" );
	}

	private static String loadBaseline(String relativeName) {
		String path = BASELINE_DIR + relativeName;
		try ( InputStream in = XuguNativeSqlBaselineTest.class.getClassLoader().getResourceAsStream( path ) ) {
			assertTrue( in != null, "missing classpath baseline: " + path );
			return new String( in.readAllBytes(), StandardCharsets.UTF_8 );
		}
		catch ( IOException e ) {
			throw new AssertionError( "failed to read baseline: " + path, e );
		}
	}

	/** Collapse runs of whitespace (space/tab/CR/LF) to a single space — matches EF SqlAssert.Normalize. */
	static String normalize(String sql) {
		return String.join( " ", sql.trim().split( "\\s+" ) );
	}
}
