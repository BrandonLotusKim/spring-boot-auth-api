package com.cheongyak.lecture.common;

import org.springframework.http.HttpStatus;

// "이 상황엔 이 HTTP 상태코드 + 이 문구" 를 담아 던지는 예외
public class ApiException extends RuntimeException {

	private final HttpStatus status;

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

}
