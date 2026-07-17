package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * I-004 / P-002: IDENTITY entity mapped to reserved-word physical table {@code ORDER}.
 * Hibernate quoted-identifier form {@code "order"} forces double-quote rendering
 * ({@link com.xugu.dialect.XuguDialect#openQuote()} / {@code closeQuote()}).
 */
@Entity
@Table(name = "\"order\"")
public class I004P002OrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

	public I004P002OrderEntity() {
	}

	public I004P002OrderEntity(String name) {
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
