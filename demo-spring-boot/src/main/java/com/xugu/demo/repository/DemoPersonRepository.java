package com.xugu.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xugu.demo.entity.DemoPerson;

public interface DemoPersonRepository extends JpaRepository<DemoPerson, Long> {
}
