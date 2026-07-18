package com.xugu.demo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Association parent (Layer B / A-SCH-012). Table prefix {@code HIB_DEMO_}.
 */
@Entity
@Table(name = "HIB_DEMO_DEPT")
public class DemoDept {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 128)
	private String name;

	@OneToMany(mappedBy = "dept", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DemoDeptMember> members = new ArrayList<>();

	protected DemoDept() {
	}

	public DemoDept(String name) {
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

	public List<DemoDeptMember> getMembers() {
		return members;
	}

	public DemoDeptMember addMember(String code, String memberName) {
		DemoDeptMember member = new DemoDeptMember( code, memberName, this );
		members.add( member );
		return member;
	}
}
