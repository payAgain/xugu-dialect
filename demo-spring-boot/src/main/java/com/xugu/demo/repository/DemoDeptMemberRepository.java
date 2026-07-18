package com.xugu.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xugu.demo.entity.DemoDeptMember;

public interface DemoDeptMemberRepository extends JpaRepository<DemoDeptMember, Long> {
}
