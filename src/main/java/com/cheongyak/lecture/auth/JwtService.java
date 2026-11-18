package com.cheongyak.lecture.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.cheongyak.lecture.member.Member;

// 로그인 성공 시 "입장권"(JWT 토큰)을 만들어 준다. 앱은 이후 요청마다 이 토큰을 보낸다
@Service
public class JwtService {

	private final JwtEncoder jwtEncoder;
	private final JwtProperties props;

	public JwtService(JwtEncoder jwtEncoder, JwtProperties props) {
		this.jwtEncoder = jwtEncoder;
		this.props = props;
	}

	public String issue(Member member) {
		Instant now = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("lecture-server")
				.issuedAt(now)
				.expiresAt(now.plus(props.expiresMinutes(), ChronoUnit.MINUTES))
				.subject(member.getUsername())          // 토큰 주인 = 아이디
				.claim("memberId", member.getId())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}

	public long expiresInSeconds() {
		return props.expiresMinutes() * 60;
	}

}
