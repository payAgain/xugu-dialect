package com.xugu.dialect.it.entities;

import java.time.Duration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * I-010 / P-002 / A-TYP-014: Duration ↔ INTERVAL DAY TO SECOND / INTERVAL SECOND entity.
 */
@Entity
@Table( name = "HIB_I010_P002_INTERVAL" )
public class I010P002IntervalEntity {

	@Id
	private Integer id;

	@JdbcTypeCode( SqlTypes.DURATION )
	@Column( name = "c_duration", nullable = false )
	private Duration duration;

	@JdbcTypeCode( SqlTypes.INTERVAL_SECOND )
	@Column( name = "c_interval_sec", nullable = false )
	private Duration intervalSecond;

	public I010P002IntervalEntity() {
	}

	public I010P002IntervalEntity(Integer id, Duration duration, Duration intervalSecond) {
		this.id = id;
		this.duration = duration;
		this.intervalSecond = intervalSecond;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Duration getIntervalSecond() {
		return intervalSecond;
	}

	public void setIntervalSecond(Duration intervalSecond) {
		this.intervalSecond = intervalSecond;
	}
}
