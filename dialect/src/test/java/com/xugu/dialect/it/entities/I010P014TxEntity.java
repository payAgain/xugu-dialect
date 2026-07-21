package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * I-010 / P-014 / XP-005: simple entity for explicit Session transaction atomicity.
 */
@Entity
@Table( name = "HIB_I010_P014_TX" )
public class I010P014TxEntity {

	@Id
	private Integer id;

	@Column( name = "label", nullable = false, length = 64 )
	private String label;

	public I010P014TxEntity() {
	}

	public I010P014TxEntity(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
