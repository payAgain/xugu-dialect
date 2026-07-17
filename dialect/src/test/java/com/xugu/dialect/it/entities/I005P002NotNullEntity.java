package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ORM probe for C-EXC-002 live path: NOT NULL violation on {@code email} field.
 */
@Entity
@Table(name = "HIB_I005_P002_NN")
public class I005P002NotNullEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", nullable = false, length = 64)
	private String email;

	public I005P002NotNullEntity() {
	}

	public I005P002NotNullEntity(String email) {
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
