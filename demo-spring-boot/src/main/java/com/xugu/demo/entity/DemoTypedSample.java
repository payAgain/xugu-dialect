package com.xugu.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Layer C′ type-field probe (A-TYP-001/002/004/005/006/008/009/010/012).
 * LOB representative is BLOB ({@code A-TYP-010}); CLOB stays dialect-it-only ({@code A-TYP-011}).
 */
@Entity
@Table(name = "HIB_DEMO_TYPED_SAMPLE")
public class DemoTypedSample {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** A-TYP-001 */
	@Column(name = "int_val", nullable = false)
	private Integer intVal;

	/** A-TYP-002 */
	@Column(name = "dec_val", nullable = false, precision = 12, scale = 2)
	private BigDecimal decVal;

	/** A-TYP-004 */
	@Column(name = "label", nullable = false, length = 64)
	private String label;

	/** A-TYP-005 */
	@Column(name = "flag_val", nullable = false)
	private Boolean flagVal;

	/** A-TYP-006 */
	@Column(name = "day_val", nullable = false)
	private LocalDate dayVal;

	/** A-TYP-008 */
	@Column(name = "ts_val", nullable = false)
	private LocalDateTime tsVal;

	/** A-TYP-009 */
	@JdbcTypeCode(SqlTypes.VARBINARY)
	@Column(name = "bin_val")
	private byte[] binVal;

	/** A-TYP-010 (BLOB LOB representative; not A-TYP-011 CLOB) */
	@Lob
	@JdbcTypeCode(SqlTypes.BLOB)
	@Column(name = "blob_val")
	private byte[] blobVal;

	/**
	 * A-TYP-012 — VARCHAR(36) + converter (not {@code SqlTypes.UUID}).
	 * Avoids Xugu JDBC E50044 on {@code UUIDJdbcType} extract; see {@link UuidAsVarcharConverter}.
	 */
	@Convert(converter = UuidAsVarcharConverter.class)
	@Column(name = "guid_val", length = 36)
	private UUID guidVal;

	protected DemoTypedSample() {
	}

	public DemoTypedSample(
			Integer intVal,
			BigDecimal decVal,
			String label,
			Boolean flagVal,
			LocalDate dayVal,
			LocalDateTime tsVal,
			byte[] binVal,
			byte[] blobVal,
			UUID guidVal) {
		this.intVal = intVal;
		this.decVal = decVal;
		this.label = label;
		this.flagVal = flagVal;
		this.dayVal = dayVal;
		this.tsVal = tsVal;
		this.binVal = binVal;
		this.blobVal = blobVal;
		this.guidVal = guidVal;
	}

	public Long getId() {
		return id;
	}

	public Integer getIntVal() {
		return intVal;
	}

	public BigDecimal getDecVal() {
		return decVal;
	}

	public String getLabel() {
		return label;
	}

	public Boolean getFlagVal() {
		return flagVal;
	}

	public LocalDate getDayVal() {
		return dayVal;
	}

	public LocalDateTime getTsVal() {
		return tsVal;
	}

	public byte[] getBinVal() {
		return binVal;
	}

	public byte[] getBlobVal() {
		return blobVal;
	}

	public UUID getGuidVal() {
		return guidVal;
	}
}
