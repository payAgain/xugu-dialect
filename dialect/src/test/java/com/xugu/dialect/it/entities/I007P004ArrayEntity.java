package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * P-004 / A-TYP-015: INTEGER ARRAY column round-trip entity.
 */
@Entity
@Table( name = "HIB_I007_P004_ARR" )
public class I007P004ArrayEntity {

	@Id
	private Integer id;

	@JdbcTypeCode( SqlTypes.ARRAY )
	@Column( name = "tags" )
	private Integer[] tags;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer[] getTags() {
		return tags;
	}

	public void setTags(Integer[] tags) {
		this.tags = tags;
	}
}
