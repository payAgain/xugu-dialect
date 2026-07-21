package com.xugu.dialect.type;

/**
 * XuGu XML type DDL ({@code reference/sql/datatype/xml.md}).
 *
 * <p><b>A-TYP-016 known-limit:</b> XuGu documents {@code XML} and {@code XMLTYPE} synonyms
 * (BLOB-backed, max 2GB). Hibernate 7.4 exposes {@code SqlTypes.SQLXML} with
 * {@code XmlJdbcType}; Xugu JDBC may not fully implement {@code java.sql.SQLXML} — dialect
 * locks documented DDL for schema export; live acceptance uses native SQL string round-trip IT.
 */
public final class XuguXmlTypeSupport {

	private XuguXmlTypeSupport() {
	}

	/** Hibernate {@code SqlTypes.SQLXML} → documented XuGu {@code XML} column type. */
	public static final String XML_DDL = "xml";

	/** Documented synonym ({@code xml.md} §1.1 — XML and XMLTYPE share underlying XML storage). */
	public static final String XMLTYPE_DDL = "xmltype";

	/** Documented maximum XML payload size (2GB text). */
	public static final long MAX_XML_LENGTH_BYTES = 2L * 1024 * 1024 * 1024;
}
