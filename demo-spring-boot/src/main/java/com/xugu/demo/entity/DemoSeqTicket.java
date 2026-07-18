package com.xugu.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * SEQUENCE-backed demo entity (Layer B / A-SEQ-003, A-SEQ-004).
 * Sequence name {@code HIB_DEMO_SEQ_TICKET_SEQ}.
 */
@Entity
@Table(name = "HIB_DEMO_SEQ_TICKET")
@SequenceGenerator(
		name = "hib_demo_seq_ticket_gen",
		sequenceName = "HIB_DEMO_SEQ_TICKET_SEQ",
		allocationSize = 1,
		initialValue = 1
)
public class DemoSeqTicket {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hib_demo_seq_ticket_gen")
	private Long id;

	@Column(nullable = false, length = 128)
	private String label;

	protected DemoSeqTicket() {
	}

	public DemoSeqTicket(String label) {
		this.label = label;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
