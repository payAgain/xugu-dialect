package com.xugu.dialect.sequence;

import org.hibernate.MappingException;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.internal.util.StringHelper;

/**
 * XuGu SEQUENCE support (A-SEQ-001..005, A-XCUT-008).
 *
 * <p><b>Locked NEXTVAL form (live DB + sequence.md):</b>
 * {@code select <seq>.nextval from dual}
 * via {@link #getSelectSequenceNextValString} + {@link #getFromDual}.
 *
 * <p><b>CURRVAL:</b> XuGu uses the function {@code CURRVAL('name')} — not {@code seq.currval}
 * (live probe: {@code seq.CURRVAL} fails). After NEXTVAL in the same session.
 *
 * <p>CREATE/DROP strings follow Hibernate {@link SequenceSupport} defaults, which match
 * documented {@code CREATE SEQUENCE … START WITH … INCREMENT BY …} / {@code DROP SEQUENCE}.
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
