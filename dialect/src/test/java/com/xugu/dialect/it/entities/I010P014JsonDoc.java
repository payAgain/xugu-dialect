package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * I-010 / P-014 / XP-004: large JSON LOB materialization probe.
 */
@Entity
@Table( name = "HIB_I010_P014_JSON" )
public class I010P014JsonDoc {

	@Id
	private Integer id;

	@JdbcTypeCode( SqlTypes.JSON )
	@Column( name = "payload", nullable = false )
	private String payload;

	public I010P014JsonDoc() {
	}

	public I010P014JsonDoc(Integer id, String payload) {
		this.id = id;
		this.payload = payload;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
