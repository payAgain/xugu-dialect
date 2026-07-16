package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Probe entity for C-DDL-002 alter-column IT. Column {@code payload} starts as integer
 * in explicit JDBC create; dialect alter string changes it to varchar.
 */
@Entity
@Table(name = "HIB_I003_P006_ALTER")
public class I003P006AlterEntity {

	@Id
	private Integer id;

	@Column(name = "payload")
	private Integer payload;

	public I003P006AlterEntity() {
	}

	public I003P006AlterEntity(Integer id, Integer payload) {
		this.id = id;
		this.payload = payload;
	}

	public Integer getId() {
		return id;
	}

	public Integer getPayload() {
		return payload;
	}
}
