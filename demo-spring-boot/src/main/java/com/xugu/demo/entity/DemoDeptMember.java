package com.xugu.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Association child with FK + UNIQUE code (Layer B / A-SCH-011, A-SCH-012).
 */
@Entity
@Table(
		name = "HIB_DEMO_DEPT_MEMBER",
		uniqueConstraints = @UniqueConstraint(
				name = "UK_HIB_DEMO_DEPT_MEMBER_CODE",
				columnNames = "code"
		)
)
public class DemoDeptMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 64)
	private String code;

	@Column(name = "name", nullable = false, length = 128)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "dept_id", nullable = false)
	private DemoDept dept;

	protected DemoDeptMember() {
	}

	public DemoDeptMember(String code, String name, DemoDept dept) {
		this.code = code;
		this.name = name;
		this.dept = dept;
	}

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DemoDept getDept() {
		return dept;
	}

	public void setDept(DemoDept dept) {
		this.dept = dept;
	}
}
