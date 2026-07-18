package com.xugu.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xugu.demo.entity.DemoJsonDoc;

public interface DemoJsonDocRepository extends JpaRepository<DemoJsonDoc, Long> {
}
