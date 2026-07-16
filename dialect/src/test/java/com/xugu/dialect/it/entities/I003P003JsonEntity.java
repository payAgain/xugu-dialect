package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "HIB_I003_P003_JSON")
public class I003P003JsonEntity {

	@Id
	private Integer id;

	@Column(name = "grp_key", nullable = false, length = 32)
	private String grpKey;

	@Column(name = "label", length = 64)
	private String label;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "payload")
	private String payload;

	public I003P003JsonEntity() {
	}

	public I003P003JsonEntity(Integer id, String grpKey, String label, String payload) {
		this.id = id;
		this.grpKey = grpKey;
		this.label = label;
		this.payload = payload;
	}

	public Integer getId() {
		return id;
	}

	public String getGrpKey() {
		return grpKey;
	}

	public String getLabel() {
		return label;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
