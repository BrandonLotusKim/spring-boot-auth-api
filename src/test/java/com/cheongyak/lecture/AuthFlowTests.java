package com.cheongyak.lecture;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

// 실제로 서버를 띄워서 회원가입 → 로그인 → 내 정보 조회 흐름을 확인한다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowTests {

	@Value("${local.server.port}")
	int port;

	private final HttpClient client = HttpClient.newHttpClient();

	private HttpResponse<String> send(String method, String path, String json, String token) throws Exception {
		HttpRequest.Builder b = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
				.header("Content-Type", "application/json")
				.method(method, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json));
		if (token != null) {
			b.header("Authorization", "Bearer " + token);
		}
		return client.send(b.build(), HttpResponse.BodyHandlers.ofString());
	}

	@Test
	void signupLoginAndMe() throws Exception {
		var signup = send("POST", "/api/auth/signup", "{\"username\":\"tester\",\"password\":\"pass1234\",\"nickname\":\"테스터\"}", null);
		assertThat(signup.statusCode()).isEqualTo(201);

		var duplicate = send("POST", "/api/auth/signup", "{\"username\":\"tester\",\"password\":\"pass1234\",\"nickname\":\"테스터\"}", null);
		assertThat(duplicate.statusCode()).isEqualTo(409);

		var wrong = send("POST", "/api/auth/login", "{\"username\":\"tester\",\"password\":\"nope\"}", null);
		assertThat(wrong.statusCode()).isEqualTo(401);

		var login = send("POST", "/api/auth/login", "{\"username\":\"tester\",\"password\":\"pass1234\"}", null);
		assertThat(login.statusCode()).isEqualTo(200);
		Matcher m = Pattern.compile("\"accessToken\"\\s*:\\s*\"([^\"]+)\"").matcher(login.body());
		assertThat(m.find()).isTrue();
		String token = m.group(1);

		var meWithout = send("GET", "/api/me", null, null);
		assertThat(meWithout.statusCode()).isEqualTo(401);

		var me = send("GET", "/api/me", null, token);
		assertThat(me.statusCode()).isEqualTo(200);
		assertThat(me.body()).contains("\"username\":\"tester\"");
	}

	@Test
	void seededAdminCanLoginAndNoticesArePublic() throws Exception {
		var login = send("POST", "/api/auth/login", "{\"username\":\"admin\",\"password\":\"1234\"}", null);
		assertThat(login.statusCode()).isEqualTo(200);

		var notices = send("GET", "/api/notices", null, null);
		assertThat(notices.statusCode()).isEqualTo(200);
		assertThat(notices.body()).contains("청라 아크원 푸르지오");

		var invalid = send("POST", "/api/auth/signup", "{\"username\":\"ab\",\"password\":\"pass1234\",\"nickname\":\"x\"}", null);
		assertThat(invalid.statusCode()).isEqualTo(400);
		assertThat(invalid.body()).contains("아이디는 4~20자입니다.");

		var broken = send("POST", "/api/auth/signup", "{broken", null);
		assertThat(broken.statusCode()).isEqualTo(400);
		assertThat(broken.body()).contains("요청 JSON 형식이 올바르지 않습니다.");

		var wrongLogin = send("POST", "/api/auth/login", "{\"username\":\"admin\",\"password\":\"9999\"}", null);
		assertThat(wrongLogin.body()).contains("아이디 또는 비밀번호가 올바르지 않습니다.");
	}

}
