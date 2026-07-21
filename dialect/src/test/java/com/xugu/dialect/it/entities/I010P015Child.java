package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity(name = "Child")
@Table(name = "HIB_I010_P015_CHILD")
public class I010P015Child {

	@Id
	private Integer id;

	@Column(nullable = false, length = 64)
	private String label;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PARENT_ID", nullable = false)
	private I010P015Parent parent;

	public I010P015Child() {
	}

	public I010P015Child(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public I010P015Parent getParent() {
		return parent;
	}

	public void setParent(I010P015Parent parent) {
		this.parent = parent;
	}
}
