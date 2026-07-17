package com.xugu.dialect;

import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.identity.XuguIdentityColumnSupport;
import com.xugu.dialect.sequence.SequenceInformationExtractorXuguDatabaseImpl;
import com.xugu.dialect.sequence.XuguSequenceSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Offline unit tests for P-005 identity + sequence SQL fragments.
 */
class XuguIdentitySequenceTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void identitySupportWired_A_IDN_001() {
		IdentityColumnSupport support = dialect.getIdentityColumnSupport();
		assertInstanceOf( XuguIdentityColumnSupport.class, support );
		assertTrue( support.supportsIdentityColumns() );
		assertEquals( "identity(1,1)", support.getIdentityColumnString( java.sql.Types.INTEGER ) );
		assertFalse( support.getIdentityColumnString( java.sql.Types.INTEGER ).toLowerCase().contains( "auto_increment" ),
				"prefer IDENTITY in NONE mode (A-IDN-002)" );
	}

	@Test
	void identitySelectFallback_A_IDN_003() {
		String select = dialect.getIdentityColumnSupport()
				.getIdentitySelectString( "t", "id", java.sql.Types.INTEGER );
		assertEquals( "select last_insert_id() from dual", select.toLowerCase() );
	}

	@Test
	void sequenceSupportWired_A_SEQ_001_003_008() {
		SequenceSupport support = dialect.getSequenceSupport();
		assertInstanceOf( XuguSequenceSupport.class, support );
		assertTrue( support.supportsSequences() );
		assertEquals( "HIB_P005_SEQ.nextval", support.getSelectSequenceNextValString( "HIB_P005_SEQ" ) );
		assertEquals( " from dual", support.getFromDual() );
		assertEquals(
				"select HIB_P005_SEQ.nextval from dual",
				support.getSequenceNextValString( "HIB_P005_SEQ" ) );
	}

	@Test
	void currvalFunctionForm_A_SEQ_004() {
		SequenceSupport support = dialect.getSequenceSupport();
		assertEquals( "currval('HIB_P005_SEQ')", support.getSelectSequencePreviousValString( "HIB_P005_SEQ" ) );
		assertEquals(
				"select currval('HIB_P005_SEQ') from dual",
				support.getSequencePreviousValString( "HIB_P005_SEQ" ) );
		assertEquals( "currval('sch.seq')", support.getSelectSequencePreviousValString( "\"sch\".\"seq\"" ) );
	}

	@Test
	void createDropSequenceStrings_A_SEQ_001_002_005() {
		SequenceSupport support = dialect.getSequenceSupport();
		assertEquals( "create sequence HIB_P005_SEQ", support.getCreateSequenceString( "HIB_P005_SEQ" ) );
		assertEquals(
				"create sequence HIB_P005_SEQ start with 1 increment by 1",
				support.getCreateSequenceString( "HIB_P005_SEQ", 1, 1 ) );
		assertEquals( "drop sequence if exists HIB_P005_SEQ", support.getDropSequenceString( "HIB_P005_SEQ" ) );
	}

	@Test
	void dialectPrefersGetGeneratedKeys() {
		assertTrue( dialect.getDefaultUseGetGeneratedKeys() );
	}

	@Test
	void sequenceMetadataQueryAndExtractorWired() {
		String query = dialect.getQuerySequencesString();
		assertNotNull( query );
		assertTrue( query.toLowerCase().contains( "all_sequences" ),
				"expected all_sequences lookup, got: " + query );
		assertInstanceOf(
				SequenceInformationExtractorXuguDatabaseImpl.class,
				dialect.getSequenceInformationExtractor() );
	}
}
