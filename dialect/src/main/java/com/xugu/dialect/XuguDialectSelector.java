package com.xugu.dialect;

import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;

/**
 * Internal dialect selection hook (C-SEL-001).
 *
 * <p>Hibernate 7.4 autodetect uses the {@link org.hibernate.engine.jdbc.dialect.spi.DialectResolver}
 * SPI ({@link XuguDialectResolver} + {@code META-INF/services}). This interface is <em>not</em> a
 * duplicate Hibernate SPI — it is a product extension point for choosing {@link XuguDialect}
 * variants from {@link DialectResolutionInfo} without forking the resolver.
 *
 * <p>P0 default: always {@code new XuguDialect(info)} regardless of {@code COMPATIBLE_MODE}
 * (integration surfaces require {@code compatiblemode=NONE} per A-XCUT-003).
 */
public interface XuguDialectSelector {

	XuguDialect selectDialect(DialectResolutionInfo info);

	/**
	 * Default selector: standard {@link XuguDialect} for all matched XuGu connections.
	 */
	final class Default implements XuguDialectSelector {

		public static final Default INSTANCE = new Default();

		private Default() {
		}

		@Override
		public XuguDialect selectDialect(DialectResolutionInfo info) {
			return new XuguDialect( info );
		}
	}
}
