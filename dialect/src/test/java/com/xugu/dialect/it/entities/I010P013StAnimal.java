package com.xugu.dialect.it.entities;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * SINGLE_TABLE inheritance root for I-010 P-013 HQL bulk boundary matrix
 * (EF TPH analogue — not Owned).
 */
@Entity(name = "I010P013StAnimal")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "KIND", discriminatorType = DiscriminatorType.STRING)
@Table(name = "HIB_I010_P013_ST_ANIMAL")
public class I010P013StAnimal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

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
