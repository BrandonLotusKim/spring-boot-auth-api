package com.cheongyak.lecture.member.dto;

import com.cheongyak.lecture.member.Member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 앱 ↔ 서버가 주고받는 JSON 모양. 엔티티(Member)를 그대로 내보내지 않는다 (비밀번호 해시 노출 방지)
public final class MemberDtos {

	private MemberDtos() {
	}

	public record SignupRequest(
			@NotBlank(message = "아이디를 입력하세요.") @Size(min = 4, max = 20, message = "아이디는 4~20자입니다.") String username,
			@NotBlank(message = "비밀번호를 입력하세요.") @Size(min = 4, max = 50, message = "비밀번호는 4~50자입니다.") String password,
			@NotBlank(message = "닉네임을 입력하세요.") @Size(max = 30, message = "닉네임은 30자 이하입니다.") String nickname) {
	}

	public record LoginRequest(
			@NotBlank(message = "아이디를 입력하세요.") String username,
			@NotBlank(message = "비밀번호를 입력하세요.") String password) {
	}

	public record MemberResponse(Long id, String username, String nickname) {
		public static MemberResponse from(Member m) {
			return new MemberResponse(m.getId(), m.getUsername(), m.getNickname());
		}
	}

	public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds, MemberResponse member) {
	}

}
