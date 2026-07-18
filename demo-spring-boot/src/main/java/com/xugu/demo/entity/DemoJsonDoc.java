package com.xugu.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Shared JSON entity for A-TYP-013, C-JSON-001, and A-FUN-017 ({@code json_value}).
 */
@Entity
@Table(name = "HIB_DEMO_JSON_DOC")
public class DemoJsonDoc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "label", nullable = false, length = 64)
	private String label;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "payload")
	private String payload;

	protected DemoJsonDoc() {
	}

	public DemoJsonDoc(String label, String payload) {
		this.label = label;
		this.payload = payload;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
