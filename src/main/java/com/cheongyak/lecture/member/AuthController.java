package com.cheongyak.lecture.member;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cheongyak.lecture.auth.JwtService;
import com.cheongyak.lecture.member.dto.MemberDtos.LoginRequest;
import com.cheongyak.lecture.member.dto.MemberDtos.MemberResponse;
import com.cheongyak.lecture.member.dto.MemberDtos.SignupRequest;
import com.cheongyak.lecture.member.dto.MemberDtos.TokenResponse;

import jakarta.validation.Valid;

// 앱이 부르는 주소(API)를 정의하는 곳. 요청 받기 → 서비스 호출 → JSON 응답
@RestController
public class AuthController {

	private final MemberService memberService;
	private final JwtService jwtService;

	public AuthController(MemberService memberService, JwtService jwtService) {
		this.memberService = memberService;
		this.jwtService = jwtService;
	}

	// 회원가입: POST /api/auth/signup
	@PostMapping("/api/auth/signup")
	@ResponseStatus(HttpStatus.CREATED)
	public MemberResponse signup(@Valid @RequestBody SignupRequest req) {
		Member member = memberService.signup(req.username(), req.password(), req.nickname());
		return MemberResponse.from(member);
	}

	// 로그인: POST /api/auth/login → 맞으면 토큰 발급
	@PostMapping("/api/auth/login")
	public TokenResponse login(@Valid @RequestBody LoginRequest req) {
		Member member = memberService.login(req.username(), req.password());
		return new TokenResponse(jwtService.issue(member), "Bearer", jwtService.expiresInSeconds(), MemberResponse.from(member));
	}

	// 내 정보: GET /api/me (Authorization: Bearer 토큰 필요)
	@GetMapping("/api/me")
	public MemberResponse me(@AuthenticationPrincipal Jwt jwt) {
		return MemberResponse.from(memberService.getByUsername(jwt.getSubject()));
	}

}
