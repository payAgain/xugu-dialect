package com.xugu.dialect.it.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HIB_I003_P006_TYPE")
public class I003P006TypeEntity {

	@Id
	private Integer id;

	@Column(name = "label", nullable = false, length = 32)
	private String label;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public I003P006TypeEntity() {
	}

	public I003P006TypeEntity(Integer id, String label, LocalDateTime createdAt) {
		this.id = id;
		this.label = label;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
