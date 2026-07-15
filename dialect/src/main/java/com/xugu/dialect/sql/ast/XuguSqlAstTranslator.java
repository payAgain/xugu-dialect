package com.xugu.dialect.sql.ast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.LockOptions;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.sql.ast.spi.SqlAstTranslatorWithMerge;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.ast.tree.select.QueryPart;
import org.hibernate.sql.ast.tree.select.QuerySpec;
import org.hibernate.sql.exec.spi.JdbcOperation;

/**
 * SQL AST translator for XuguDB.
 *
 * <p>Default Hibernate {@code StandardSqlAstTranslator} emits ANSI
 * {@code OFFSET ? ROWS FETCH FIRST ? ROWS ONLY}, which XuGu rejects
 * ({@code [E19132] unexpected OFFSET}). This translator renders
 * {@code LIMIT count [OFFSET offset]} via {@link #renderLimitOffsetClause},
 * matching {@link com.xugu.dialect.pagination.XuguLimitHandler}.
 *
 * <p>Guards mirror MySQL 7.4.5 {@code MySQLSqlAstTranslator.visitOffsetFetchClause}:
 * skip when the current query part is already being emulated via row-numbering.
 * Unlike MySQL's {@code renderCombinedLimitClause} ({@code LIMIT offset,count}),
 * XuGu uses the LimitHandler-stable form {@code LIMIT count OFFSET offset}.
 *
 * <p>When a pessimistic lock is present, LIMIT is deferred until after
 * {@code FOR UPDATE} (and inserted before a trailing {@code NOWAIT}/{@code WAIT}),
 * matching XuGu {@code select_no_parens} order and {@code XuguLimitHandler}.
 */
public class XuguSqlAstTranslator<T extends JdbcOperation> extends SqlAstTranslatorWithMerge<T> {

	/** Trailing XuGu wait clause after FOR UPDATE (same idea as XuguLimitHandler). */
	private static final Pattern TRAILING_WAIT =
			Pattern.compile( "(?i)\\s+(nowait|wait(?:\\s+\\d+)?)\\s*$" );

	private QueryPart pendingLimitQueryPart;

	public XuguSqlAstTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
		super( sessionFactory, statement );
	}

	@Override
	public void visitOffsetFetchClause(QueryPart queryPart) {
		if ( isRowNumberingCurrentQueryPart() ) {
			return;
		}
		// Defer LIMIT until after FOR UPDATE when locking (XuGu: FOR UPDATE before LIMIT).
		if ( queryPart instanceof QuerySpec && hasPessimisticLock() ) {
			pendingLimitQueryPart = queryPart;
			return;
		}
		renderLimitOffsetClause( queryPart );
	}

	@Override
	protected void visitForUpdateClause(QuerySpec querySpec) {
		try {
			super.visitForUpdateClause( querySpec );
		}
		finally {
			if ( pendingLimitQueryPart == querySpec ) {
				pendingLimitQueryPart = null;
				renderLimitOffsetClauseAfterLock( querySpec );
			}
		}
	}

	private boolean hasPessimisticLock() {
		final LockOptions lockOptions = getLockOptions();
		return lockOptions != null
				&& lockOptions.getLockMode() != null
				&& lockOptions.getLockMode().isPessimistic();
	}

	/**
	 * Render LIMIT after FOR UPDATE; if Dialect lock string already appended
	 * {@code WAIT}/{@code NOWAIT}, insert LIMIT before that trailing clause.
	 */
	private void renderLimitOffsetClauseAfterLock(QuerySpec querySpec) {
		final StringBuilder sql = getSqlBuffer();
		final Matcher wait = TRAILING_WAIT.matcher( sql.toString() );
		String waitClause = null;
		if ( wait.find() ) {
			waitClause = wait.group();
			sql.setLength( wait.start() );
		}
		renderLimitOffsetClause( querySpec );
		if ( waitClause != null ) {
			sql.append( waitClause );
		}
	}
}
