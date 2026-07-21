package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * XP-002 entity: HQL count / group-by count materialization probe (I-010 / P-012).
 */
@Entity
@Table(name = "HIB_I010_P012_CUSTOMER")
public class I010P012Customer {

	@Id
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

	@Column(nullable = false, length = 64)
	private String city;

	public I010P012Customer() {
	}

	public I010P012Customer(Integer id, String name, String city) {
		this.id = id;
		this.name = name;
		this.city = city;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
