package com.xugu.dialect.type;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcLiteralFormatter;
import org.hibernate.type.descriptor.jdbc.JdbcType;

/**
 * XuGu INTERVAL JDBC binding for Hibernate {@link Duration}.
 *
 * <p>XuGu JDBC exposes INTERVAL columns as strings (see native IT / {@code datetime.md}
 * SQL_STANDARD literals). Default Hibernate {@code DurationJdbcType} binds NUMERIC and
 * cannot round-trip XuGu {@code INTERVAL DAY TO SECOND} / {@code INTERVAL SECOND}.
 *
 * <p>Write formats match documented / live-proven literals:
 * <ul>
 *   <li>{@link SqlTypes#DURATION} → {@code D HH:MM:SS[.fraction]} (day to second)</li>
 *   <li>{@link SqlTypes#INTERVAL_SECOND} → decimal seconds (e.g. {@code 45.55})</li>
 * </ul>
 * Read path accepts SQL_STANDARD, ISO-8601 ({@code P…}), and simple decimal seconds
 * (output may vary with server {@code DEF_INTERVAL_STYLE}).
 */
public final class XuguIntervalJdbcType implements JdbcType {

	public static final XuguIntervalJdbcType DURATION = new XuguIntervalJdbcType( SqlTypes.DURATION );
	public static final XuguIntervalJdbcType INTERVAL_SECOND = new XuguIntervalJdbcType( SqlTypes.INTERVAL_SECOND );

	private static final Pattern DAY_TO_SECOND = Pattern.compile(
			"^\\s*(-)?(?:(\\d+)\\s+)?(\\d{1,2}):(\\d{2}):(\\d{2})(?:\\.(\\d+))?\\s*$" );
	private static final Pattern PLAIN_SECONDS = Pattern.compile( "^\\s*(-)?(\\d+)(?:\\.(\\d+))?\\s*$" );

	private final int sqlTypeCode;

	private XuguIntervalJdbcType(int sqlTypeCode) {
		this.sqlTypeCode = sqlTypeCode;
	}

	@Override
	public int getJdbcTypeCode() {
		return Types.OTHER;
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return sqlTypeCode;
	}

	@Override
	public Class<?> getPreferredJavaTypeClass(WrapperOptions options) {
		return Duration.class;
	}

	@Override
	public boolean isComparable() {
		return true;
	}

	@Override
	public String toString() {
		return sqlTypeCode == SqlTypes.DURATION
				? "XuguIntervalJdbcType(DURATION)"
				: "XuguIntervalJdbcType(INTERVAL_SECOND)";
	}

	@Override
	public <T> JdbcLiteralFormatter<T> getJdbcLiteralFormatter(JavaType<T> javaType) {
		return (appender, value, dialect, wrapperOptions) -> {
			final Duration duration = javaType.unwrap( value, Duration.class, wrapperOptions );
			appender.appendSql( '\'' );
			appender.appendSql( format( duration ) );
			appender.appendSql( '\'' );
		};
	}

	@Override
	public <X> ValueBinder<X> getBinder(JavaType<X> javaType) {
		return new BasicBinder<>( javaType, this ) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
					throws SQLException {
				st.setString( index, format( javaType.unwrap( value, Duration.class, options ) ) );
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				st.setString( name, format( javaType.unwrap( value, Duration.class, options ) ) );
			}
		};
	}

	@Override
	public <X> ValueExtractor<X> getExtractor(JavaType<X> javaType) {
		return new BasicExtractor<>( javaType, this ) {
			@Override
			protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
				return javaType.wrap( parse( rs.getString( paramIndex ) ), options );
			}

			@Override
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
					throws SQLException {
				return javaType.wrap( parse( statement.getString( index ) ), options );
			}

			@Override
			protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
					throws SQLException {
				return javaType.wrap( parse( statement.getString( name ) ), options );
			}
		};
	}

	public String format(Duration duration) {
		if ( duration == null ) {
			return null;
		}
		if ( sqlTypeCode == SqlTypes.INTERVAL_SECOND ) {
			return formatSeconds( duration );
		}
		return formatDayToSecond( duration );
	}

	public Duration parse(String raw) {
		if ( raw == null ) {
			return null;
		}
		final String text = raw.trim();
		if ( text.isEmpty() ) {
			return null;
		}
		if ( text.regionMatches( true, 0, "P", 0, 1 ) ) {
			return Duration.parse( text.toUpperCase( Locale.ROOT ) );
		}
		final Matcher dts = DAY_TO_SECOND.matcher( text );
		if ( dts.matches() ) {
			return parseDayToSecond( dts );
		}
		final Matcher sec = PLAIN_SECONDS.matcher( text );
		if ( sec.matches() ) {
			return parsePlainSeconds( sec );
		}
		throw new IllegalArgumentException( "Unsupported XuGu INTERVAL literal: " + raw );
	}

	static String formatDayToSecond(Duration duration) {
		final boolean negative = duration.isNegative();
		Duration abs = negative ? duration.negated() : duration;
		final long days = abs.toDays();
		abs = abs.minusDays( days );
		final int hours = abs.toHoursPart();
		final int minutes = abs.toMinutesPart();
		final int seconds = abs.toSecondsPart();
		final int nanos = abs.getNano();
		final String body;
		if ( nanos == 0 ) {
			body = String.format( Locale.ROOT, "%d %02d:%02d:%02d", days, hours, minutes, seconds );
		}
		else {
			final String frac = String.format( Locale.ROOT, "%09d", nanos ).replaceAll( "0+$", "" );
			body = String.format( Locale.ROOT, "%d %02d:%02d:%02d.%s", days, hours, minutes, seconds, frac );
		}
		return negative ? "-" + body : body;
	}

	static String formatSeconds(Duration duration) {
		final BigDecimal seconds = BigDecimal.valueOf( duration.getSeconds() )
				.add( BigDecimal.valueOf( duration.getNano(), 9 ) );
		return seconds.stripTrailingZeros().toPlainString();
	}

	private static Duration parseDayToSecond(Matcher m) {
		final boolean negative = m.group( 1 ) != null;
		final long days = m.group( 2 ) == null ? 0L : Long.parseLong( m.group( 2 ) );
		final int hours = Integer.parseInt( m.group( 3 ) );
		final int minutes = Integer.parseInt( m.group( 4 ) );
		final int seconds = Integer.parseInt( m.group( 5 ) );
		final int nanos = fractionToNanos( m.group( 6 ) );
		Duration d = Duration.ofDays( days )
				.plusHours( hours )
				.plusMinutes( minutes )
				.plusSeconds( seconds )
				.plusNanos( nanos );
		return negative ? d.negated() : d;
	}

	private static Duration parsePlainSeconds(Matcher m) {
		final boolean negative = m.group( 1 ) != null;
		final long seconds = Long.parseLong( m.group( 2 ) );
		final int nanos = fractionToNanos( m.group( 3 ) );
		Duration d = Duration.ofSeconds( seconds, nanos );
		return negative ? d.negated() : d;
	}

	private static int fractionToNanos(String fraction) {
		if ( fraction == null || fraction.isEmpty() ) {
			return 0;
		}
		final String padded = ( fraction + "000000000" ).substring( 0, 9 );
		return Integer.parseInt( padded );
	}

	/**
	 * Compare durations ignoring sub-micro noise that JDBC/string round-trips may drop.
	 */
	public static boolean approximatelyEqual(Duration expected, Duration actual) {
		if ( expected == null || actual == null ) {
			return expected == actual;
		}
		final BigDecimal e = toSeconds( expected ).setScale( 6, RoundingMode.HALF_UP );
		final BigDecimal a = toSeconds( actual ).setScale( 6, RoundingMode.HALF_UP );
		return e.compareTo( a ) == 0;
	}

	private static BigDecimal toSeconds(Duration d) {
		return BigDecimal.valueOf( d.getSeconds() ).add( BigDecimal.valueOf( d.getNano(), 9 ) );
	}
}
