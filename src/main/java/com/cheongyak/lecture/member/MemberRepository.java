package com.cheongyak.lecture.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// 인터페이스만 선언하면 Spring Data JPA 가 SQL 을 대신 만들어 준다
// findByUsername → select * from members where username = ?
public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByUsername(String username);

	boolean existsByUsername(String username);

}
