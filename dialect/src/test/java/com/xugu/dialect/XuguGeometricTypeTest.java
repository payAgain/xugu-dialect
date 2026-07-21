package com.xugu.dialect;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguGeometricTypeSupport;
import com.xugu.dialect.type.XuguGeometricTypeSupport.Kind;
import com.xugu.dialect.type.XuguPointJdbcType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Offline unit tests: geometric type hooks (A-TYP-017).
 *
 * <p>DDL wired for {@code SqlTypes.POINT}/{@code GEOMETRY}; entity ORM uses
 * {@link XuguPointJdbcType} (string bind). Non-POINT subtypes remain native/tooling only.
 */
class XuguGeometricTypeTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void pointTypeHooksWired_A_TYP_017() {
		assertEquals( XuguGeometricTypeSupport.POINT_DDL, dialect.columnType( SqlTypes.POINT ) );
		assertEquals( XuguGeometricTypeSupport.GEOMETRY_DDL, dialect.columnType( SqlTypes.GEOMETRY ) );
	}

	@Test
	void allDocumentedKindsLocked_A_TYP_017() {
		assertEquals( 7, XuguGeometricTypeSupport.documentedKinds().length );
		assertEquals( "point", Kind.POINT.ddl() );
		assertEquals( "line", Kind.LINE.ddl() );
		assertEquals( "lseg", Kind.LSEG.ddl() );
		assertEquals( "box", Kind.BOX.ddl() );
		assertEquals( "path", Kind.PATH.ddl() );
		assertEquals( "polygon", Kind.POLYGON.ddl() );
		assertEquals( "circle", Kind.CIRCLE.ddl() );
		assertEquals( "box", XuguGeometricTypeSupport.ddlFor( Kind.BOX ) );
	}

	@Test
	void pointJdbcTypesContributed_A_TYP_017() {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.build();
		SessionFactoryImplementor sf = null;
		try {
			sf = (SessionFactoryImplementor) new MetadataSources( registry )
					.buildMetadata()
					.buildSessionFactory();
			JdbcType point = sf.getTypeConfiguration().getJdbcTypeRegistry()
					.getDescriptor( SqlTypes.POINT );
			JdbcType geometry = sf.getTypeConfiguration().getJdbcTypeRegistry()
					.getDescriptor( SqlTypes.GEOMETRY );
			assertInstanceOf( XuguPointJdbcType.class, point );
			assertInstanceOf( XuguPointJdbcType.class, geometry );
			assertEquals( SqlTypes.POINT, point.getDefaultSqlTypeCode() );
			assertEquals( SqlTypes.GEOMETRY, geometry.getDefaultSqlTypeCode() );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Test
	void pointJdbcTypeNormalize_A_TYP_017() {
		assertEquals( "(1,1)", XuguPointJdbcType.normalize( "  (1,1)  " ) );
		assertEquals( null, XuguPointJdbcType.normalize( null ) );
	}
}
