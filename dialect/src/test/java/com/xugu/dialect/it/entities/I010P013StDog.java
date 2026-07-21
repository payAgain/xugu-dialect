package com.xugu.dialect.it.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity(name = "I010P013StDog")
@DiscriminatorValue("Dog")
public class I010P013StDog extends I010P013StAnimal {

	private String barkSound;

	public String getBarkSound() {
		return barkSound;
	}

	public void setBarkSound(String barkSound) {
		this.barkSound = barkSound;
	}
}
