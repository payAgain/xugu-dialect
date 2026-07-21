package com.xugu.dialect.it.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity(name = "Parent")
@Table(name = "HIB_I010_P015_PARENT")
public class I010P015Parent {

	@Id
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<I010P015Child> children = new ArrayList<>();

	public I010P015Parent() {
	}

	public I010P015Parent(Integer id, String name) {
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

	public List<I010P015Child> getChildren() {
		return children;
	}

	public void setChildren(List<I010P015Child> children) {
		this.children = children;
	}

	public void addChild(I010P015Child child) {
		children.add( child );
		child.setParent( this );
	}
}
