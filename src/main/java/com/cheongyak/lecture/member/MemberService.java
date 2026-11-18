package com.cheongyak.lecture.member;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cheongyak.lecture.common.ApiException;

// 회원가입·로그인 규칙(비즈니스 로직)이 모이는 곳. 컨트롤러는 여기만 부른다
@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Member signup(String username, String rawPassword, String nickname) {
		if (memberRepository.existsByUsername(username)) {
			throw new ApiException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
		}
		String hash = passwordEncoder.encode(rawPassword);
		return memberRepository.save(new Member(username, hash, nickname));
	}

	@Transactional(readOnly = true)
	public Member login(String username, String rawPassword) {
		return memberRepository.findByUsername(username)
				.filter(m -> passwordEncoder.matches(rawPassword, m.getPasswordHash()))
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."));
	}

	@Transactional(readOnly = true)
	public Member getByUsername(String username) {
		return memberRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
	}

}
