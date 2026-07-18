package com.xugu.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xugu.demo.entity.DemoDept;

public interface DemoDeptRepository extends JpaRepository<DemoDept, Long> {
}
