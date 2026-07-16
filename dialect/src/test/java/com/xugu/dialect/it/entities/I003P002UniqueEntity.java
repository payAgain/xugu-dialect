package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "HIB_I003_P002_UNQ",
		uniqueConstraints = @UniqueConstraint(name = "UK_HIB_I003_P002_CODE", columnNames = "code")
)
public class I003P002UniqueEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 64)
	private String code;

	@Column(name = "label", length = 64)
	private String label;

	public I003P002UniqueEntity() {
	}

	public I003P002UniqueEntity(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
