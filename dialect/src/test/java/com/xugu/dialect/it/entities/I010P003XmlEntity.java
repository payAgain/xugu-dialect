package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * I-010 / P-003 / A-TYP-016: String ↔ XML column via {@code @JdbcTypeCode(SQLXML)}.
 */
@Entity
@Table( name = "HIB_I010_P003_XML" )
public class I010P003XmlEntity {

	@Id
	private Integer id;

	@JdbcTypeCode( SqlTypes.SQLXML )
	@Column( name = "c_xml", nullable = false )
	private String xml;

	public I010P003XmlEntity() {
	}

	public I010P003XmlEntity(Integer id, String xml) {
		this.id = id;
		this.xml = xml;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}
}
