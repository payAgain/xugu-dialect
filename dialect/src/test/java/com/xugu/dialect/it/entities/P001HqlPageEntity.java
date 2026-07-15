package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HIB_P001_HQL_PAGE")
public class P001HqlPageEntity {

	@Id
	private Integer id;

	@Column(nullable = false, length = 32)
	private String name;

	public P001HqlPageEntity() {
	}

	public P001HqlPageEntity(Integer id, String name) {
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
}
