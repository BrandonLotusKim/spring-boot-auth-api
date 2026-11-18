package com.cheongyak.lecture.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

// application.yml 의 app.jwt.* 값을 담는 클래스
@ConfigurationProperties("app.jwt")
public record JwtProperties(String secret, long expiresMinutes) {
}
