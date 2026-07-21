package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Nullable String/Integer columns for I-010/P-016 null-semantics IT (XP-008).
 */
@Entity
@Table(name = "HIB_I010_P016_NULL")
public class I010P016NullEntity {

	@Id
	private Integer id;

	@Column(name = "name", nullable = true, length = 64)
	private String name;

	@Column(name = "qty", nullable = true)
	private Integer qty;

	public I010P016NullEntity() {
	}

	public I010P016NullEntity(Integer id, String name, Integer qty) {
		this.id = id;
		this.name = name;
		this.qty = qty;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getQty() {
		return qty;
	}
}
