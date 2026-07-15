package com.xugu.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.UUID;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Timeouts;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.NationalizationSupport;
import org.hibernate.dialect.TimeZoneSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.lock.spi.LockingSupport;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.query.sqm.CastType;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JsonJdbcType;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

import com.xugu.dialect.identity.XuguIdentityColumnSupport;
import com.xugu.dialect.internal.XuguKeywords;
import com.xugu.dialect.internal.XuguLockingSupport;
import com.xugu.dialect.pagination.XuguLimitHandler;
import com.xugu.dialect.sequence.XuguSequenceSupport;

import jakarta.persistence.Timeout;

/**
 * XuguDB dialect for Hibernate 7.4 — types, DDL, pagination, locks, identity, sequences
 * (P-003/P-004/P-005). Extends {@link Dialect} only (no MySQL/Oracle dialect inheritance).
 *
 * <p><b>TIMESTAMP vs DATETIME (A-TYP-008):</b> Hibernate timestamp SqlTypes map to
 * Xugu {@code TIMESTAMP} (not {@code DATETIME}). Both exist under
 * {@code reference/sql/datatype/datetime.md}; TIMESTAMP is chosen for consistency with
 * Hibernate's timestamp codes and documented fractional-second precision (0–6, default 3).
 * Note: Xugu TIMESTAMP may auto-fill current time when the column is omitted on INSERT;
 * IT always binds explicit values.
 *
 * <p><b>Pagination (A-PAG-*):</b> {@link XuguLimitHandler} emits
 * {@code LIMIT count} / {@code LIMIT count OFFSET offset} with JDBC bind markers
 * (not {@code FETCH FIRST}, not {@code LIMIT offset,count}). With locks, live XuGu
 * requires {@code FOR UPDATE} before {@code LIMIT} (and {@code WAIT} after LIMIT when
 * both are present). The handler does not use Hibernate's default
 * {@code LIMIT…FOR UPDATE} insertion.
 *
 * <p><b>Lock timeout mapping (A-LCK-003):</b> XuGu {@code WAIT wait_ms} is
 * <em>milliseconds</em> ({@code reference/sql/select/select.md} {@code opt_wait}).
 * Hibernate {@link Timeout#milliseconds()} is also milliseconds — values are passed
 * through <em>without</em> converting to seconds. Docs place {@code NOWAIT}/{@code WAIT}
 * on parenthesized selects after {@code FOR UPDATE}; Dialect strings append the wait
 * token after {@code FOR UPDATE} (e.g. {@code for update nowait},
 * {@code for update wait 2000}). IT verifies executability; parenthesized form is a
 * documented fallback.
 *
 * <p><b>Identity (A-IDN-*):</b> {@link XuguIdentityColumnSupport} emits
 * {@code identity(1,1)} (prefer IDENTITY over AUTO_INCREMENT in NONE mode).
 * Generated keys: JDBC {@code getGeneratedKeys} (driver-documented); select fallback
 * {@code select last_insert_id() from dual}.
 *
 * <p><b>Sequence (A-SEQ-*, A-XCUT-008):</b> {@link XuguSequenceSupport} locks
 * {@code select &lt;seq&gt;.nextval from dual}; CURRVAL via {@code currval('name')}.
 *
 * <p><b>Not emitted:</b> {@code SKIP LOCKED} (A-LCK-004), {@code FOR SHARE} (A-LCK-005).
 *
 * <p><b>A-LCK-005 (FOR SHARE / pessimistic read) — Hibernate shim only:</b>
 * XuGu documents {@code FOR UPDATE} / {@code FOR READ ONLY}, not {@code FOR SHARE}.
 * The matrix status remains <em>文档不允许</em> for the FOR SHARE surface.
 * {@link #getReadLockString} maps {@code PESSIMISTIC_READ} to exclusive
 * {@code FOR UPDATE} semantics so Hibernate lock APIs still emit executable SQL —
 * this is <em>not</em> share-lock support. Concurrent readers may block; applications
 * must not assume PostgreSQL-style {@code FOR SHARE} / non-blocking concurrent reads.
 */
public class XuguDialect extends Dialect {

	public XuguDialect() {
		super( DatabaseVersion.make( 12, 0 ) );
		for ( String keyword : XuguKeywords.RESERVED ) {
			registerKeyword( keyword );
		}
	}

	// -------------------------------------------------------------------------
	// Types (A-TYP-*)
	// -------------------------------------------------------------------------

	@Override
	protected String columnType(int sqlTypeCode) {
		return switch ( sqlTypeCode ) {
			case SqlTypes.BOOLEAN -> "boolean";
			case SqlTypes.TINYINT -> "tinyint";
			case SqlTypes.SMALLINT -> "smallint";
			case SqlTypes.INTEGER -> "integer";
			case SqlTypes.BIGINT -> "bigint";
			case SqlTypes.FLOAT, SqlTypes.REAL -> "float";
			case SqlTypes.DOUBLE -> "double";
			case SqlTypes.NUMERIC, SqlTypes.DECIMAL -> "numeric($p,$s)";
			case SqlTypes.DATE -> "date";
			case SqlTypes.TIME -> "time($p)";
			case SqlTypes.TIME_WITH_TIMEZONE -> "time($p) with time zone";
			case SqlTypes.TIMESTAMP, SqlTypes.TIMESTAMP_UTC -> "timestamp($p)";
			case SqlTypes.TIMESTAMP_WITH_TIMEZONE -> "timestamp($p) with time zone";
			case SqlTypes.CHAR, SqlTypes.NCHAR -> "char($l)";
			case SqlTypes.VARCHAR, SqlTypes.NVARCHAR,
					SqlTypes.LONGVARCHAR, SqlTypes.LONGNVARCHAR,
					SqlTypes.LONG32VARCHAR, SqlTypes.LONG32NVARCHAR -> "varchar($l)";
			case SqlTypes.CLOB, SqlTypes.NCLOB,
					SqlTypes.MATERIALIZED_CLOB, SqlTypes.MATERIALIZED_NCLOB -> "clob";
			// binary.md documents bare BINARY only (no length param); length capped via getMaxVarbinaryLength()
			case SqlTypes.BINARY, SqlTypes.VARBINARY -> "binary";
			case SqlTypes.LONGVARBINARY, SqlTypes.LONG32VARBINARY -> "blob";
			case SqlTypes.BLOB, SqlTypes.MATERIALIZED_BLOB -> "blob";
			case SqlTypes.UUID -> "guid";
			case SqlTypes.JSON -> "json";
			default -> super.columnType( sqlTypeCode );
		};
	}

	@Override
	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.registerColumnTypes( typeContributions, serviceRegistry );
		final DdlTypeRegistry ddlTypes = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
		ddlTypes.addDescriptor( new DdlTypeImpl( SqlTypes.UUID, columnType( SqlTypes.UUID ), this ) );
		ddlTypes.addDescriptor( new DdlTypeImpl( SqlTypes.JSON, columnType( SqlTypes.JSON ), this ) );
	}

	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		super.contributeTypes( typeContributions, serviceRegistry );
		final JdbcTypeRegistry jdbcTypes = typeContributions.getTypeConfiguration().getJdbcTypeRegistry();
		jdbcTypes.addDescriptorIfAbsent( UUIDJdbcType.INSTANCE );
		jdbcTypes.addDescriptorIfAbsent( JsonJdbcType.INSTANCE );
	}

	@Override
	public int getPreferredSqlTypeCodeForBoolean() {
		return SqlTypes.BOOLEAN;
	}

	@Override
	public String toBooleanValueString(boolean bool) {
		return bool ? "true" : "false";
	}

	@Override
	public void appendBooleanValueString(SqlAppender appender, boolean bool) {
		appender.appendSql( toBooleanValueString( bool ) );
	}

	@Override
	public NationalizationSupport getNationalizationSupport() {
		// NCHAR/NVARCHAR/NCLOB are documented synonyms of CHAR/VARCHAR/CLOB
		return NationalizationSupport.IMPLICIT;
	}

	@Override
	public boolean stripsTrailingSpacesFromChar() {
		// character.md: CHAR/VARCHAR trim trailing spaces on insert
		return true;
	}

	@Override
	public int getMaxVarcharLength() {
		return 60_000;
	}

	@Override
	public int getMaxNVarcharLength() {
		return getMaxVarcharLength();
	}

	@Override
	public int getMaxVarbinaryLength() {
		// binary.md: BINARY max 64KB
		return 65_536;
	}

	@Override
	public int getDefaultDecimalPrecision() {
		// numerical.md: unspecified NUMERIC defaults to NUMERIC(12,0)
		return 12;
	}

	@Override
	public int getDefaultTimestampPrecision() {
		// datetime.md: TIMESTAMP default precision 3
		return 3;
	}

	@Override
	public int getFloatPrecision() {
		return 24;
	}

	@Override
	public int getDoublePrecision() {
		return 53;
	}

	@Override
	public long getFractionalSecondPrecisionInNanos() {
		// Xugu displays up to millisecond precision for TIME/TIMESTAMP
		return 1_000_000L;
	}

	@Override
	public TimeZoneSupport getTimeZoneSupport() {
		return TimeZoneSupport.NATIVE;
	}

	@Override
	public void appendUUIDLiteral(SqlAppender appender, UUID literal) {
		appender.appendSql( "cast('" );
		appender.appendSql( literal.toString() );
		appender.appendSql( "' as guid)" );
	}

	@Override
	public String castPattern(CastType from, CastType to) {
		// type_conversion.md: CAST(expr AS type) is supported
		return super.castPattern( from, to );
	}

	// -------------------------------------------------------------------------
	// DDL helpers (A-DDL-*) — defaults match Xugu CREATE/ALTER/DROP docs
	// -------------------------------------------------------------------------

	@Override
	public String getCreateTableString() {
		return "create table";
	}

	@Override
	public String getAlterTableString(String tableName) {
		return "alter table " + tableName;
	}

	@Override
	public String getAddColumnString() {
		return "add column";
	}

	@Override
	public String getDropTableString(String tableName) {
		return "drop table " + tableName;
	}

	@Override
	public String getNullColumnString() {
		return "";
	}

	@Override
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " add constraint " + constraintName + " primary key ";
	}

	// -------------------------------------------------------------------------
	// Identifiers & keywords (A-XCUT-001/002/007)
	// -------------------------------------------------------------------------

	@Override
	public char openQuote() {
		return '"';
	}

	@Override
	public char closeQuote() {
		return '"';
	}

	@Override
	public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData)
			throws SQLException {
		builder.setUnquotedCaseStrategy( IdentifierCaseStrategy.UPPER );
		builder.setQuotedCaseStrategy( IdentifierCaseStrategy.MIXED );
		builder.applyReservedWords( getKeywords() );
		return super.buildIdentifierHelper( builder, dbMetaData );
	}

	// -------------------------------------------------------------------------
	// Identity & Sequence (A-IDN-*, A-SEQ-*, A-XCUT-008)
	// -------------------------------------------------------------------------

	@Override
	public IdentityColumnSupport getIdentityColumnSupport() {
		return XuguIdentityColumnSupport.INSTANCE;
	}

	@Override
	public SequenceSupport getSequenceSupport() {
		return XuguSequenceSupport.INSTANCE;
	}

	// -------------------------------------------------------------------------
	// Pagination (A-PAG-001/002/003) — no FETCH FIRST (A-PAG-005)
	// -------------------------------------------------------------------------

	@Override
	public LimitHandler getLimitHandler() {
		return XuguLimitHandler.INSTANCE;
	}

	// -------------------------------------------------------------------------
	// Locks (A-LCK-001/002/003) — no SKIP LOCKED / FOR SHARE (A-LCK-004/005)
	// -------------------------------------------------------------------------

	@Override
	public LockingSupport getLockingSupport() {
		return XuguLockingSupport.INSTANCE;
	}

	@Override
	public String getForUpdateString() {
		return " for update";
	}

	@Override
	public String getForUpdateString(String aliases) {
		return getForUpdateString() + " of " + aliases;
	}

	@Override
	public String getForUpdateString(Timeout timeout) {
		return appendWait( getForUpdateString(), timeout );
	}

	@Override
	public String getForUpdateString(String aliases, LockOptions lockOptions) {
		final LockMode lockMode = lockOptions.getLockMode();
		final Timeout timeout = lockOptions.getTimeout();
		return switch ( lockMode ) {
			case PESSIMISTIC_READ, PESSIMISTIC_WRITE, PESSIMISTIC_FORCE_INCREMENT,
					UPGRADE_NOWAIT, UPGRADE_SKIPLOCKED, WRITE ->
					appendWait( getForUpdateString( aliases ), timeout );
			default -> "";
		};
	}

	@Override
	public String getForUpdateNowaitString() {
		return getForUpdateString() + " nowait";
	}

	@Override
	public String getForUpdateNowaitString(String aliases) {
		return getForUpdateString( aliases ) + " nowait";
	}

	/**
	 * A-LCK-004: XuGu has no SKIP LOCKED — do not invent the keyword.
	 * With {@code supportsSkipLocked=false}, Hibernate write-lock path should not call this.
	 */
	@Override
	public String getForUpdateSkipLockedString() {
		return getForUpdateString();
	}

	@Override
	public String getForUpdateSkipLockedString(String aliases) {
		return getForUpdateString( aliases );
	}

	@Override
	public String getWriteLockString(Timeout timeout) {
		return appendWait( getForUpdateString(), timeout );
	}

	@Override
	public String getWriteLockString(int timeoutMillis) {
		return appendWait( getForUpdateString(), Timeout.milliseconds( timeoutMillis ) );
	}

	@Override
	public String getWriteLockString(String aliases, Timeout timeout) {
		return appendWait( getForUpdateString( aliases ), timeout );
	}

	@Override
	public String getWriteLockString(String aliases, int timeoutMillis) {
		return appendWait( getForUpdateString( aliases ), Timeout.milliseconds( timeoutMillis ) );
	}

	/**
	 * A-LCK-005: Hibernate shim only — XuGu has no FOR SHARE.
	 * Maps pessimistic read to exclusive {@code FOR UPDATE} (not share-lock support).
	 * Concurrent readers may block; apps must not assume FOR SHARE. Matrix: 文档不允许.
	 */
	@Override
	public String getReadLockString(Timeout timeout) {
		return getWriteLockString( timeout );
	}

	@Override
	public String getReadLockString(int timeoutMillis) {
		return getWriteLockString( timeoutMillis );
	}

	@Override
	public String getReadLockString(String aliases, Timeout timeout) {
		return getWriteLockString( aliases, timeout );
	}

	@Override
	public String getReadLockString(String aliases, int timeoutMillis) {
		return getWriteLockString( aliases, timeoutMillis );
	}

	/**
	 * Append XuGu {@code nowait} / {@code wait &lt;ms&gt;} after FOR UPDATE.
	 * Hibernate timeout milliseconds map 1:1 to XuGu WAIT milliseconds (no seconds conversion).
	 */
	private static String appendWait(String forUpdate, Timeout timeout) {
		final int ms = timeout.milliseconds();
		if ( ms == Timeouts.NO_WAIT_MILLI || ms == 0 ) {
			return forUpdate + " nowait";
		}
		if ( ms == Timeouts.SKIP_LOCKED_MILLI ) {
			// supportsSkipLocked=false — never emit SKIP LOCKED
			return forUpdate;
		}
		if ( Timeouts.isRealTimeout( timeout ) ) {
			return forUpdate + " wait " + ms;
		}
		// WAIT_FOREVER / unknown magic → plain FOR UPDATE (default wait forever)
		return forUpdate;
	}
}
