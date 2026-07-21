package com.xugu.dialect.it.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Fixed seed datetime for I-010/P-016 temporal projection IT (XP-009).
 */
@Entity
@Table(name = "HIB_I010_P016_TEMP")
public class I010P016TemporalEntity {

	@Id
	private Integer id;

	@Column(name = "label", nullable = false, length = 32)
	private String label;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	public I010P016TemporalEntity() {
	}

	public I010P016TemporalEntity(Integer id, String label, LocalDateTime createdAt) {
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
