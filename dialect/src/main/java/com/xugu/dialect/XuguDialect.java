package com.xugu.dialect;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.UUID;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.NationalizationSupport;
import org.hibernate.dialect.TimeZoneSupport;
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

import com.xugu.dialect.internal.XuguKeywords;

/**
 * XuguDB dialect for Hibernate 7.4 — types, DDL helpers, identifiers (P-003).
 * Extends {@link Dialect} only (no MySQL/Oracle dialect inheritance).
 *
 * <p><b>TIMESTAMP vs DATETIME (A-TYP-008):</b> Hibernate timestamp SqlTypes map to
 * Xugu {@code TIMESTAMP} (not {@code DATETIME}). Both exist under
 * {@code reference/sql/datatype/datetime.md}; TIMESTAMP is chosen for consistency with
 * Hibernate's timestamp codes and documented fractional-second precision (0–6, default 3).
 * Note: Xugu TIMESTAMP may auto-fill current time when the column is omitted on INSERT;
 * IT always binds explicit values.
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
}
