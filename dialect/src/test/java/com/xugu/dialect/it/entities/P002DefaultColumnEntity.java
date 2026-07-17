package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.ColumnDefault;

/**
 * SchemaExport probe for A-DDL-005: column DEFAULT clause in CREATE TABLE DDL.
 */
@Entity
@Table(name = "HIB_P002_DEFAULT_PROBE")
public class P002DefaultColumnEntity {

	@Id
	@Column(nullable = false)
	private Integer id;

	@Column(name = "status", nullable = false, length = 32)
	@ColumnDefault("'active'")
	private String status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
