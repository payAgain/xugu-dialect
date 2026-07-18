package com.xugu.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xugu.demo.entity.DemoSeqTicket;

public interface DemoSeqTicketRepository extends JpaRepository<DemoSeqTicket, Long> {
}
