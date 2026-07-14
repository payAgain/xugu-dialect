package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * SchemaExport probe for A-TYP-009: VARBINARY → bare {@code BINARY} (no length param).
 */
@Entity
@Table(name = "HIB_P003_BINARY_PROBE")
public class P003BinaryEntity {

	@Id
	@Column(nullable = false)
	private Integer id;

	@JdbcTypeCode(SqlTypes.VARBINARY)
	@Column(nullable = false)
	private byte[] payload;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}
