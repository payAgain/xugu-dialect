package com.xugu.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.UUID;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.Timeouts;
import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.NationalizationSupport;
import org.hibernate.dialect.TimeZoneSupport;
import org.hibernate.dialect.aggregate.AggregateSupport;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.lock.spi.LockingSupport;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.dialect.temptable.TemporaryTableKind;
import org.hibernate.dialect.temptable.TemporaryTableStrategy;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.ViolatedConstraintNameExtractor;
import org.hibernate.tool.schema.extract.spi.SequenceInformationExtractor;
import org.hibernate.engine.jdbc.env.spi.IdentifierCaseStrategy;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.CheckConstraint;
import org.hibernate.query.sqm.CastType;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.SqlAstTranslatorFactory;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.spi.StandardSqlAstTranslatorFactory;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.exec.spi.JdbcOperation;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.UUIDJdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

import com.xugu.dialect.aggregate.XuguAggregateSupport;
import com.xugu.dialect.exception.XuguSQLExceptionConversionDelegate;
import com.xugu.dialect.exception.XuguViolatedConstraintNameExtractor;
import com.xugu.dialect.function.XuguFunctionRegistrations;
import com.xugu.dialect.identity.XuguIdentityColumnSupport;
import com.xugu.dialect.internal.XuguKeywords;
import com.xugu.dialect.internal.XuguLockingSupport;
import com.xugu.dialect.pagination.XuguLimitHandler;
import com.xugu.dialect.sequence.SequenceInformationExtractorXuguDatabaseImpl;
import com.xugu.dialect.sequence.XuguSequenceSupport;
import com.xugu.dialect.sql.ast.XuguSqlAstTranslator;
import com.xugu.dialect.temptable.XuguGlobalTemporaryTableStrategy;
import com.xugu.dialect.temptable.XuguLocalTemporaryTableStrategy;
import com.xugu.dialect.type.XuguCastingJsonArrayJdbcTypeConstructor;
import com.xugu.dialect.type.XuguCastingJsonJdbcType;

import jakarta.persistence.Timeout;

/**
 * XuguDB dialect for Hibernate 7.4 — types, DDL, pagination, locks, identity, sequences,
 * schema/temp/comment/constraints (P-003…P-007). Extends {@link Dialect} only
 * (no MySQL/Oracle dialect inheritance).
 *
 * <p><b>TIMESTAMP vs DATETIME (A-TYP-008):</b> Hibernate timestamp SqlTypes map to
 * Xugu {@code TIMESTAMP} (not {@code DATETIME}). Both exist under
 * {@code reference/sql/datatype/datetime.md}; TIMESTAMP is chosen for consistency with
 * Hibernate's timestamp codes and documented fractional-second precision (0–6, default 3).
 * Note: Xugu TIMESTAMP may auto-fill current time when the column is omitted on INSERT;
 * IT always binds explicit values.
 *
 * <p><b>Sequence metadata (hbm2ddl validate):</b> {@link #getQuerySequencesString()}
 * reads documented {@code ALL_SEQUENCES}; {@link #getSequenceInformationExtractor()}
 * maps {@code seq_name}/{@code min_val}/{@code max_val}/{@code step_val} so validate
 * sees existing sequences (avoids false {@code missing sequence}).
 *
 * <p><b>Exception mapping (C-EXC-* / I-003):</b>
 * {@link #buildSQLExceptionConversionDelegate()} maps documented Xugu error codes
 * (unique/FK/check/not-null, deadlock, lock timeout) to Hibernate exception types;
 * {@link #getViolatedConstraintNameExtractor()} parses field names when present
 * (e.g. E16005 not-null messages).
 *
 * <p><b>Pagination (A-PAG-*):</b> {@link XuguLimitHandler} emits
 * {@code LIMIT count} / {@code LIMIT count OFFSET offset} with JDBC bind markers
 * (not {@code FETCH FIRST}, not {@code LIMIT offset,count}). HQL/Criteria pagination
 * goes through {@link XuguSqlAstTranslator} (via {@link #getSqlAstTranslatorFactory()})
 * so the AST path does not emit ANSI {@code OFFSET}/{@code FETCH}. With locks, live XuGu
 * requires {@code FOR UPDATE} before {@code LIMIT} (and {@code WAIT} after LIMIT when
 * both are present). The handler / translator do not use Hibernate's default
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
 * <p><b>Functions (A-FUN-* / C-JSON-*):</b> {@link #initializeFunctionRegistry} contributes
 * XuGu-native templates via {@link XuguFunctionRegistrations}. Primary UUID SQL:
 * {@code uuid()}. JSON: {@code json_value}/{@code json_extract} plus
 * {@code json_arrayagg}/{@code json_objectagg}; JDBC writes use {@code cast(? as json)};
 * {@link #getAggregateSupport()} covers JSON embeddable component paths.
 * Hibernate {@code listagg} → XuGu {@code LISTAGG … WITHIN GROUP}.
 *
 * <p><b>Schema / temp / comment / constraints (A-SCH-* , P-007):</b>
 * {@code CREATE}/{@code DROP SCHEMA}; schema-qualified names
 * ({@link NameQualifierSupport#SCHEMA}, catalog deferred A-SCH-003);
 * local temp via {@link XuguLocalTemporaryTableStrategy}; global temp via
 * {@link XuguGlobalTemporaryTableStrategy} (requires {@code support_global_tab=ON});
 * {@code COMMENT ON TABLE|COLUMN}; UNIQUE/FK/CHECK; {@code TRUNCATE TABLE};
 * basic {@code CREATE [UNIQUE] INDEX}. Temp-table FK is <em>文档不允许</em>
 * (A-SCH-007) — never emitted by the temp-table exporter.
 *
 * <p><b>Not emitted:</b> {@code SKIP LOCKED} (A-LCK-004), {@code FOR SHARE} (A-LCK-005),
 * FK on temporary tables (A-SCH-007), catalog.schema.table (A-SCH-003 deferred).
 *
 * <p><b>A-LCK-005 (FOR SHARE / pessimistic read) — Hibernate shim only:</b>
 * XuGu documents {@code FOR UPDATE} / {@code FOR READ ONLY}, not {@code FOR SHARE}.
 * The matrix status remains <em>文档不允许</em> for the FOR SHARE surface.
 * {@link #getReadLockString} maps {@code PESSIMISTIC_READ} to exclusive
 * {@code FOR UPDATE} semantics so Hibernate lock APIs still emit executable SQL —
 * this is <em>not</em> share-lock support. Concurrent readers may block; applications
 * must not assume PostgreSQL-style {@code FOR SHARE} / non-blocking concurrent reads.
 *
 * <p><b>Isolation (A-XCUT-005 / A-XCUT-006):</b> XuGu {@code ISO_LEVEL} documents
 * {@code 1=READ COMMITTED} (default), {@code 2=REPEATABLE READ},
 * {@code 3=SERIALIZABLE} ({@code reference/.../iso_level.md}).
 * <em>READ UNCOMMITTED is not a XuGu ISO_LEVEL value</em> — this dialect does
 * <strong>not</strong> claim RU support (文档不允许). Hibernate's Dialect surface
 * exposes only {@link #doesReadCommittedCauseWritersToBlockReaders()} /
 * {@link #doesRepeatableReadCauseReadersToBlockWriters()}; session isolation is
 * otherwise set via JDBC / XuGu session parameters.
 *
 * <p><b>compatible_mode (A-XCUT-003):</b> Integration and demo surfaces use
 * {@code compatible_mode=NONE} (JDBC {@code compatiblemode=NONE}); no MySQL/Oracle
 * compatible-mode dependency.
 */
public class XuguDialect extends Dialect {

	/** Default version when JDBC metadata does not supply one (live observed major=12). */
	public static final DatabaseVersion MINIMUM_VERSION = DatabaseVersion.make( 12, 0 );

	private final UniqueDelegate uniqueDelegate = new CreateTableUniqueDelegate( this );

	public XuguDialect() {
		this( MINIMUM_VERSION );
	}

	/**
	 * Version-aware constructor (A-SPI-003).
	 */
	public XuguDialect(DatabaseVersion version) {
		super( version != null ? version : MINIMUM_VERSION );
		registerXuguKeywords();
	}

	/**
	 * SPI constructor: copy version from JDBC resolution info when present.
	 */
	public XuguDialect(DialectResolutionInfo info) {
		this( info.makeCopyOrDefault( MINIMUM_VERSION ) );
		registerKeywords( info );
	}

	private void registerXuguKeywords() {
		for ( String keyword : XuguKeywords.RESERVED ) {
			registerKeyword( keyword );
		}
	}

	// -------------------------------------------------------------------------
	// Isolation notes (A-XCUT-005 / A-XCUT-006) — Hibernate Dialect hooks only
	// -------------------------------------------------------------------------

	/**
	 * XuGu READ COMMITTED does not require writers to block readers for typical
	 * ORM workloads; leave Hibernate default {@code false}.
	 */
	@Override
	public boolean doesReadCommittedCauseWritersToBlockReaders() {
		return false;
	}

	/**
	 * XuGu REPEATABLE READ does not require readers to block writers for typical
	 * ORM workloads; leave Hibernate default {@code false}.
	 */
	@Override
	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
		return false;
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
		jdbcTypes.addDescriptorIfAbsent( SqlTypes.JSON, XuguCastingJsonJdbcType.INSTANCE );
		jdbcTypes.addTypeConstructorIfAbsent( XuguCastingJsonArrayJdbcTypeConstructor.INSTANCE );
	}

	@Override
	public AggregateSupport getAggregateSupport() {
		return XuguAggregateSupport.INSTANCE;
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
	// SQL functions (A-FUN-001..014, 016..018) — P-006
	// -------------------------------------------------------------------------

	@Override
	public void initializeFunctionRegistry(FunctionContributions functionContributions) {
		super.initializeFunctionRegistry( functionContributions );
		XuguFunctionRegistrations.register( functionContributions );
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
	// Schema / temp / comment / constraints / truncate / index (A-SCH-*) — P-007
	// -------------------------------------------------------------------------

	/**
	 * A-SCH-002: qualify with schema only. Catalog (database) qualifier is deferred
	 * (A-SCH-003) — Xugu “database” ≠ Hibernate catalog always.
	 */
	@Override
	public NameQualifierSupport getNameQualifierSupport() {
		return NameQualifierSupport.SCHEMA;
	}

	/** A-SCH-001: {@code CREATE SCHEMA schema_name} ({@code reference/object/schema.md}). */
	@Override
	public boolean canCreateSchema() {
		return true;
	}

	@Override
	public String[] getCreateSchemaCommand(String schemaName) {
		return new String[] { "create schema " + schemaName };
	}

	/** A-SCH-001: {@code DROP SCHEMA schema_name} (default RESTRICT). */
	@Override
	public String[] getDropSchemaCommand(String schemaName) {
		return new String[] { "drop schema " + schemaName };
	}

	/** Resolve current schema for tooling ({@code SELECT CURRENT_SCHEMA()}). */
	@Override
	public String getCurrentSchemaCommand() {
		return "select current_schema()";
	}

	/**
	 * Preferred mutation temp-table kind: local (always available).
	 * Global requires {@code support_global_tab=ON} — see {@link XuguGlobalTemporaryTableStrategy}.
	 */
	@Override
	public TemporaryTableKind getSupportedTemporaryTableKind() {
		return TemporaryTableKind.LOCAL;
	}

	@Override
	public TemporaryTableStrategy getLocalTemporaryTableStrategy() {
		return XuguLocalTemporaryTableStrategy.INSTANCE;
	}

	/**
	 * Returns global-temp DDL strategy always (strings locked for apps/tests).
	 * Live CREATE requires {@code support_global_tab=ON} (A-SCH-005 precondition).
	 */
	@Override
	public TemporaryTableStrategy getGlobalTemporaryTableStrategy() {
		return XuguGlobalTemporaryTableStrategy.INSTANCE;
	}

	@Override
	public String getTemporaryTableCreateCommand() {
		return XuguLocalTemporaryTableStrategy.CREATE_COMMAND;
	}

	@Override
	public String getTemporaryTableCreateOptions() {
		return XuguLocalTemporaryTableStrategy.CREATE_OPTIONS;
	}

	@Override
	public String getTemporaryTableDropCommand() {
		return "drop table";
	}

	@Override
	public String getTemporaryTableTruncateCommand() {
		return "truncate table";
	}

	/**
	 * A-SCH-008/009: Hibernate {@code StandardTableExporter} emits
	 * {@code comment on table … is '…'} / {@code comment on column … is '…'}
	 * when this returns true and {@link #getTableComment}/{@link #getColumnComment}
	 * return empty (no inline suffix on CREATE).
	 */
	@Override
	public boolean supportsCommentOn() {
		return true;
	}

	/**
	 * Empty → schema export uses separate {@code COMMENT ON} (A-SCH-008/009).
	 * For the documented inline alternate on CREATE TABLE, use
	 * {@link #inlineTableComment(String)} (A-SCH-010).
	 */
	@Override
	public String getTableComment(String comment) {
		return "";
	}

	@Override
	public String getColumnComment(String comment) {
		return "";
	}

	/**
	 * A-SCH-010 alternate: {@code CREATE TABLE … COMMENT 'text'}
	 * ({@code reference/object/table/create.md} {@code opt_comment}).
	 * Not used by Hibernate schema export when {@link #supportsCommentOn()} is true.
	 */
	public static String inlineTableComment(String comment) {
		return " comment '" + escapeComment( comment ) + "'";
	}

	/**
	 * Documented inline column comment on CREATE: {@code col TYPE COMMENT 'text'}.
	 */
	public static String inlineColumnComment(String comment) {
		return " comment '" + escapeComment( comment ) + "'";
	}

	/** Locked {@code COMMENT ON TABLE} form for unit/IT assertions (A-SCH-008). */
	public static String commentOnTableSql(String tableName, String comment) {
		return "comment on table " + tableName + " is '" + escapeComment( comment ) + "'";
	}

	/** Locked {@code COMMENT ON COLUMN} form for unit/IT assertions (A-SCH-009). */
	public static String commentOnColumnSql(String qualifiedColumn, String comment) {
		return "comment on column " + qualifiedColumn + " is '" + escapeComment( comment ) + "'";
	}

	/**
	 * A-SCH-011: prefer unique constraints on CREATE TABLE (and ALTER ADD when migrating).
	 */
	@Override
	public UniqueDelegate getUniqueDelegate() {
		return uniqueDelegate;
	}

	/**
	 * A-SCH-012: {@code ALTER TABLE … ADD CONSTRAINT … FOREIGN KEY (…) REFERENCES …}.
	 * Matches {@code reference/object/constraints.md}. Default Dialect form is correct;
	 * override kept for locked fragment documentation in tests.
	 */
	@Override
	public String getAddForeignKeyConstraintString(
			String constraintName,
			String[] foreignKey,
			String referencedTable,
			String[] primaryKey,
			boolean referencesPrimaryKey) {
		final StringBuilder sb = new StringBuilder( 64 );
		sb.append( " add constraint " )
				.append( quote( constraintName ) )
				.append( " foreign key (" )
				.append( String.join( ",", foreignKey ) )
				.append( ") references " )
				.append( referencedTable );
		if ( !referencesPrimaryKey ) {
			sb.append( " (" ).append( String.join( ",", primaryKey ) ).append( ')' );
		}
		return sb.toString();
	}

	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String foreignKeyDefinition) {
		return " add constraint " + quote( constraintName ) + " " + foreignKeyDefinition;
	}

	/** A-SCH-014: drop FK / unique via {@code DROP CONSTRAINT}. */
	@Override
	public String getDropForeignKeyString() {
		return "drop constraint";
	}

	@Override
	public String getDropUniqueKeyString() {
		return "drop constraint";
	}

	/** A-SCH-013: CHECK constraints documented. */
	@Override
	public boolean supportsColumnCheck() {
		return true;
	}

	@Override
	public boolean supportsTableCheck() {
		return true;
	}

	@Override
	public String getCheckConstraintString(CheckConstraint checkConstraint) {
		return super.getCheckConstraintString( checkConstraint );
	}

	/** A-SCH-012: ON DELETE CASCADE etc. documented under key_actions. */
	@Override
	public boolean supportsCascadeDelete() {
		return true;
	}

	/**
	 * A-SCH-015: {@code TRUNCATE [TABLE] name}
	 * ({@code reference/object/table/truncate.md}).
	 */
	@Override
	public String getTruncateTableStatement(String tableName) {
		return "truncate table " + tableName;
	}

	/**
	 * A-SCH-016: {@code CREATE [UNIQUE] INDEX … ON … (…)}
	 * ({@code reference/object/indexes.md}).
	 */
	@Override
	public String getCreateIndexString(boolean unique) {
		return unique ? "create unique index" : "create index";
	}

	@Override
	public boolean qualifyIndexName() {
		return true;
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
		builder.setNameQualifierSupport( getNameQualifierSupport() );
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
	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
		return XuguSQLExceptionConversionDelegate.create( getViolatedConstraintNameExtractor() );
	}

	@Override
	public ViolatedConstraintNameExtractor getViolatedConstraintNameExtractor() {
		return XuguViolatedConstraintNameExtractor.INSTANCE;
	}

	@Override
	public SequenceSupport getSequenceSupport() {
		return XuguSequenceSupport.INSTANCE;
	}

	/**
	 * Documented system view {@code ALL_SEQUENCES}
	 * ({@code reference/system-view/all/all_sequences.md}).
	 */
	@Override
	public String getQuerySequencesString() {
		return "select * from all_sequences";
	}

	@Override
	public SequenceInformationExtractor getSequenceInformationExtractor() {
		return SequenceInformationExtractorXuguDatabaseImpl.INSTANCE;
	}

	// -------------------------------------------------------------------------
	// Pagination (A-PAG-001/002/003) — no FETCH FIRST (A-PAG-005)
	// -------------------------------------------------------------------------

	@Override
	public LimitHandler getLimitHandler() {
		return XuguLimitHandler.INSTANCE;
	}

	/**
	 * HQL/Criteria pagination: use {@link XuguSqlAstTranslator} so offset/fetch
	 * become {@code LIMIT count [OFFSET offset]} (not ANSI OFFSET/FETCH).
	 */
	@Override
	public SqlAstTranslatorFactory getSqlAstTranslatorFactory() {
		return new StandardSqlAstTranslatorFactory() {
			@Override
			protected <T extends JdbcOperation> SqlAstTranslator<T> buildTranslator(
					SessionFactoryImplementor sessionFactory, Statement statement) {
				return new XuguSqlAstTranslator<>( sessionFactory, statement );
			}
		};
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
