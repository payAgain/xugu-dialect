package com.xugu.dialect;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.Dialect;

/**
 * Placeholder XuguDB dialect for P-001 scaffold.
 * Extends {@link Dialect} only — no MySQL/Oracle dialect inheritance.
 */
public class XuguDialect extends Dialect {

	public XuguDialect() {
		super(DatabaseVersion.make(12, 0));
	}
}
