package com.xugu.dialect;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.junit.jupiter.api.Test;

import com.xugu.dialect.type.XuguXmlJdbcType;
import com.xugu.dialect.type.XuguXmlTypeSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Offline unit tests: XML type hooks (A-TYP-016).
 *
 * <p>DDL wired for {@code SqlTypes.SQLXML}; entity ORM uses {@link XuguXmlJdbcType}
 * (string bind). Standard {@code XmlJdbcType}/{@code java.sql.SQLXML} is not recommended.
 */
class XuguXmlTypeTest {

	private final XuguDialect dialect = new XuguDialect();

	@Test
	void xmlTypeHooksWired_A_TYP_016() {
		assertEquals( XuguXmlTypeSupport.XML_DDL, dialect.columnType( SqlTypes.SQLXML ) );
	}

	@Test
	void documentedXmlConstantsLocked_A_TYP_016() {
		assertEquals( "xml", XuguXmlTypeSupport.XML_DDL );
		assertEquals( "xmltype", XuguXmlTypeSupport.XMLTYPE_DDL );
		assertEquals( 2L * 1024 * 1024 * 1024, XuguXmlTypeSupport.MAX_XML_LENGTH_BYTES );
	}

	@Test
	void xmlJdbcTypeContributed_A_TYP_016() {
		StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySetting( JdbcSettings.DIALECT, XuguDialect.class.getName() )
				.build();
		SessionFactoryImplementor sf = null;
		try {
			sf = (SessionFactoryImplementor) new MetadataSources( registry )
					.buildMetadata()
					.buildSessionFactory();
			JdbcType sqlxml = sf.getTypeConfiguration().getJdbcTypeRegistry()
					.getDescriptor( SqlTypes.SQLXML );
			assertInstanceOf( XuguXmlJdbcType.class, sqlxml );
			assertEquals( SqlTypes.SQLXML, sqlxml.getDefaultSqlTypeCode() );
		}
		finally {
			if ( sf != null ) {
				sf.close();
			}
			StandardServiceRegistryBuilder.destroy( registry );
		}
	}

	@Test
	void xmlJdbcTypeNormalize_A_TYP_016() {
		assertEquals( "<num>1</num>", XuguXmlJdbcType.normalize( "  <num>1</num>  " ) );
		assertEquals( null, XuguXmlJdbcType.normalize( null ) );
	}
}
