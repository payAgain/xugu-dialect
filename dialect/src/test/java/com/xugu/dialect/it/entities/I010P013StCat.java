package com.xugu.dialect.it.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "I010P013StCat")
@DiscriminatorValue("Cat")
public class I010P013StCat extends I010P013StAnimal {

	private Integer whiskerLength;

	public Integer getWhiskerLength() {
		return whiskerLength;
	}

	public void setWhiskerLength(Integer whiskerLength) {
		this.whiskerLength = whiskerLength;
	}
}
