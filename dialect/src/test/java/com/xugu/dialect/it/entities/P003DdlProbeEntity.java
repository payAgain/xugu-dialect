package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HIB_P003_DDL_PROBE")
public class P003DdlProbeEntity {

	@Id
	@Column(nullable = false)
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

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
