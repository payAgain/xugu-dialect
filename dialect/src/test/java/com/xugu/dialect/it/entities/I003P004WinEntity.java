package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HIB_I003_P004_WIN")
public class I003P004WinEntity {

	@Id
	private Integer id;

	@Column(name = "grp_key", nullable = false, length = 32)
	private String grpKey;

	@Column(name = "score", nullable = false)
	private Integer score;

	public I003P004WinEntity() {
	}

	public I003P004WinEntity(Integer id, String grpKey, Integer score) {
		this.id = id;
		this.grpKey = grpKey;
		this.score = score;
	}

	public Integer getId() {
		return id;
	}

	public String getGrpKey() {
		return grpKey;
	}

	public Integer getScore() {
		return score;
	}
}
