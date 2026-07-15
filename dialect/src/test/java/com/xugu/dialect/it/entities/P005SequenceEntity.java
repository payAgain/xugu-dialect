package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "HIB_P005_SEQ_ENT")
@SequenceGenerator(
		name = "hib_p005_seq_gen",
		sequenceName = "HIB_P005_SEQ_GEN",
		allocationSize = 1,
		initialValue = 1
)
public class P005SequenceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_p005_seq_gen")
	private Long id;

	@Column(nullable = false, length = 64)
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
