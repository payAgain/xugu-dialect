package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * I-004 / P-001: {@link GenerationType#AUTO} (Integer, non-UUID) so Hibernate emits a physical sequence.
 */
@Entity
@Table(name = "HIB_I004_P001_AUTO")
public class I004P001AutoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

	public I004P001AutoEntity() {
	}

	public I004P001AutoEntity(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
