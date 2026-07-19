package com.xugu.dialect.sequence;

import org.hibernate.MappingException;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.internal.util.StringHelper;

/**
 * XuGu SEQUENCE support (A-SEQ-001..006, A-XCUT-008).
 *
 * <p><b>Locked NEXTVAL form (live DB + sequence.md):</b>
 * {@code select <seq>.nextval from dual}
 * via {@link #getSelectSequenceNextValString} + {@link #getFromDual}.
 *
 * <p><b>CURRVAL:</b> XuGu uses the function {@code CURRVAL('name')} — not {@code seq.currval}
 * (live probe: {@code seq.CURRVAL} fails). After NEXTVAL in the same session.
 *
 * <p><b>ALTER SEQUENCE (A-SEQ-006):</b> XuGu documents {@code ALTER SEQUENCE … START WITH …},
 * {@code INCREMENT BY …}, {@code MINVALUE … MAXVALUE …}. Hibernate's default
 * {@code getRestartSequenceString} emits ANSI {@code RESTART WITH} which XuGu rejects
 * (live E19132); this dialect maps restart to {@code START WITH}. See
 * {@code reference/object/sequence.md}.
 *
 * <p>CREATE strings follow Hibernate {@link SequenceSupport} defaults
 * ({@code CREATE SEQUENCE … START WITH … INCREMENT BY …}).
 * DROP uses XuGu-documented {@code DROP SEQUENCE IF EXISTS …}
 * ({@code reference/object/sequence.md}) so schema create-drop / {@code GenerationType.AUTO}
 * does not halt on missing sequences (E7002).
 */
public class XuguSequenceSupport implements SequenceSupport {

	public static final XuguSequenceSupport INSTANCE = new XuguSequenceSupport();

	@Override
	public String getSelectSequenceNextValString(String sequenceName) {
		return sequenceName + ".nextval";
	}

	@Override
	public String getSelectSequencePreviousValString(String sequenceName) throws MappingException {
		return "currval('" + unquoteForCurrvalArg( sequenceName ) + "')";
	}

	@Override
	public String getFromDual() {
		return " from dual";
	}

	/**
	 * Idempotent DROP (docs: {@code DROP SEQUENCE IF EXISTS}).
	 */
	@Override
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence if exists " + sequenceName;
	}

	/**
	 * A-SEQ-006: XuGu {@code ALTER SEQUENCE name START WITH n} — not {@code RESTART WITH}
	 * (documented {@code reference/object/sequence.md}; live-proven NONE mode).
	 */
	@Override
	public String getRestartSequenceString(String sequenceName, long startValue) {
		return "alter sequence " + sequenceName + " start with " + startValue;
	}

	/** Documented ALTER form for live IT / tooling ({@code reference/object/sequence.md}). */
	public static String alterSequenceIncrementBy(String sequenceName, int increment) {
		return "alter sequence " + sequenceName + " increment by " + increment;
	}

	/**
	 * Strip identifier quotes so CURRVAL receives a plain name or {@code schema.seq} string.
	 */
	static String unquoteForCurrvalArg(String sequenceName) {
		String s = StringHelper.unquote( sequenceName );
		// Handle "schema"."seq" → schema.seq
		if ( s.indexOf( '"' ) >= 0 ) {
			StringBuilder sb = new StringBuilder( s.length() );
			boolean inQuote = false;
			for ( int i = 0; i < s.length(); i++ ) {
				char c = s.charAt( i );
				if ( c == '"' ) {
					inQuote = !inQuote;
					continue;
				}
				sb.append( c );
			}
			return sb.toString();
		}
		return s;
	}
}
