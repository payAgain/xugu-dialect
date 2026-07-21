package com.xugu.dialect.it.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * I-010 / P-004 / A-TYP-017: String ↔ POINT / bounded GEOMETRY→POINT via {@code @JdbcTypeCode}.
 */
@Entity
@Table( name = "HIB_I010_P004_POINT" )
public class I010P004PointEntity {

	@Id
	private Integer id;

	@JdbcTypeCode( SqlTypes.POINT )
	@Column( name = "c_point", nullable = false )
	private String point;

	@JdbcTypeCode( SqlTypes.GEOMETRY )
	@Column( name = "c_geometry", nullable = false )
	private String geometry;

	public I010P004PointEntity() {
	}

	public I010P004PointEntity(Integer id, String point, String geometry) {
		this.id = id;
		this.point = point;
		this.geometry = geometry;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getGeometry() {
		return geometry;
	}

	public void setGeometry(String geometry) {
		this.geometry = geometry;
	}
}
