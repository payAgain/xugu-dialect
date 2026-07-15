package com.xugu.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Demo entity. Table prefix {@code HIB_DEMO_} for easy cleanup / identification.
 */
@Entity
@Table(name = "HIB_DEMO_PERSON")
public class DemoPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 128)
	private String name;

	protected DemoPerson() {
	}

	public DemoPerson(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "DemoPerson{id=" + id + ", name='" + name + "'}";
	}
}
