package com.xugu.dialect.it.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity(name = "I003P005BulkEngineer")
@Table(name = "HIB_I003_P005_ENGINEER")
public class I003P005BulkEngineer extends I003P005BulkPerson {

	private boolean fellow;

	public boolean isFellow() {
		return fellow;
	}

	public void setFellow(boolean fellow) {
		this.fellow = fellow;
	}
}
