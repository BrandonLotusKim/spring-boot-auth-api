package com.cheongyak.lecture.hello;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 1단계: 서버가 살아서 JSON 을 돌려주는지 확인하는 가장 작은 API
@RestController
public class HelloController {

	@GetMapping("/api/hello")
	public Map<String, String> hello() {
		return Map.of(
				"message", "안녕하세요, 청약소식 서버입니다",
				"time", Instant.now().toString());
	}

}
