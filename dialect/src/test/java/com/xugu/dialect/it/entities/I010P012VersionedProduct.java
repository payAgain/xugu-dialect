package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * XP-001 entity: {@code @Version} optimistic concurrency probe (I-010 / P-012).
 */
@Entity
@Table(name = "HIB_I010_P012_PRODUCT")
public class I010P012VersionedProduct {

	@Id
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

	@Version
	@Column(nullable = false)
	private Long version;

	public I010P012VersionedProduct() {
	}

	public I010P012VersionedProduct(Integer id, String name) {
		this.id = id;
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
