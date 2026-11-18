package com.cheongyak.lecture.member;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

// 회원 테이블(members)과 1:1 로 연결되는 클래스. 필드 = 컬럼
@Entity
@Table(name = "members")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 20)
	private String username;

	@Column(nullable = false)
	private String passwordHash;   // 비밀번호 원문이 아니라 BCrypt 해시를 저장

	@Column(nullable = false, length = 30)
	private String nickname;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	protected Member() {
		// JPA 가 객체를 만들 때 쓰는 기본 생성자
	}

	public Member(String username, String passwordHash, String nickname) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.nickname = nickname;
	}

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getNickname() {
		return nickname;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

}
