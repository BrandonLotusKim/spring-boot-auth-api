# Spring Boot 인증 API 서버

회원가입, 로그인(JWT), 내 정보 조회, 공고 목록 API를 가진 작은 Spring Boot 서버입니다.
3인 팀 프로젝트 「청약소식」에서 팀원들에게 Spring Boot 백엔드 구조를 가르치기 위해 만들었고, 팀 서버의 출발점으로 썼습니다.

| 항목 | 내용 |
|---|---|
| 언어 / 버전 | Java 17, Spring Boot 4.1.2 |
| 데이터 | Spring Data JPA (Hibernate) · `local` = H2 메모리 DB / `supabase` = PostgreSQL |
| 인증 | BCrypt 비밀번호 해시 + JWT(HS256), Spring Security OAuth2 Resource Server |
| 빌드 · 배포 | Gradle wrapper, 실행 jar, systemd 서비스 파일(AWS EC2 Ubuntu 기준) |
| 테스트 | 통합 테스트 3개 (회원가입→로그인→내 정보, 시드 계정 로그인·공개 API, 컨텍스트 로딩) |

## 설계하면서 정한 것

- **계층 분리**: Controller는 요청·응답만, 규칙은 Service, 저장은 Repository(JPA). 팀원이 기능을 추가할 때 어디에 무엇을 넣을지 바로 알 수 있게 했습니다.
- **세션 없는 인증**: 모바일 앱 전용 API라 서버 세션을 쓰지 않고(STATELESS) `Authorization: Bearer` 토큰만 검사합니다. 별도 필터를 직접 만들지 않고 Spring Security의 Resource Server 기능을 그대로 썼습니다.
- **오류 응답 통일**: 어디서 예외가 나든 앱은 항상 `{ "message": "..." }` 하나만 처리하면 되도록 `@RestControllerAdvice`로 모았습니다. 입력값 검증 실패와 JSON 형식 오류도 같은 모양으로 돌려줍니다.
- **환경 분리**: 설치 없이 바로 돌아가는 `local`(H2)과 실제 DB용 `supabase`(PostgreSQL) 프로필을 나눴고, DB 접속 정보와 JWT 비밀키는 환경 변수로만 받습니다.
- **겪은 함정**: `/error` 경로를 인증 대상으로 두면 입력 오류(400)가 인증 실패(401)로 바뀌어 보입니다. 그래서 `/error`는 열어 두었습니다.

## 1. 준비물

```powershell
winget install EclipseAdoptium.Temurin.17.JDK   # JDK 17
java -version                                  # 17.x 가 나오면 OK
```

VS Code 확장 3개:
- Extension Pack for Java (`vscjava.vscode-java-pack`)
- Spring Boot Extension Pack (`vmware.vscode-boot-dev-pack`)
- REST Client (`humao.rest-client`)

## 2. 실행 (개발 중)

```powershell
.\gradlew.bat bootRun        # Mac/Linux: ./gradlew bootRun
```

- http://localhost:8080/api/hello
- `requests.http` 를 열어 Send Request 로 API 시험
- 연습용 계정: `admin` / `1234`
- DB 들여다보기: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:lecture`, 사용자 `sa`)

## 3. 실행 파일(jar) 만들기

```powershell
.\gradlew.bat bootJar        # → build\libs\lecture-server.jar
java -jar build\libs\lecture-server.jar
```

테스트까지 같이: `.\gradlew.bat build`

## 4. 실제 PostgreSQL(Supabase)에 연결

```powershell
$env:SPRING_PROFILES_ACTIVE="supabase"
$env:DB_URL="jdbc:postgresql://<호스트>:5432/postgres?sslmode=require"
$env:DB_USERNAME="<사용자>"
$env:DB_PASSWORD="<비밀번호>"
$env:JWT_SECRET="<32자 이상 문자열>"
java -jar build\libs\lecture-server.jar
```

## 5. 서버(Ubuntu, AWS EC2)에 올리기

```bash
# 내 PC → 서버로 jar 복사
scp -i key.pem build/libs/lecture-server.jar ubuntu@<서버IP>:/opt/lecture-server/

# 서버에서
sudo apt update && sudo apt install -y openjdk-17-jre-headless
cp app.env.example /opt/lecture-server/app.env   # 값 채우기
sudo cp deploy/lecture-server.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable --now lecture-server
curl http://localhost:8080/actuator/health
```

AWS 보안 그룹에서 8080 포트(또는 앞단 Nginx 80/443)를 열어야 밖에서 접속된다.

## API

| 메서드 | 주소 | 로그인 | 설명 |
|---|---|---|---|
| GET | `/actuator/health` | - | 서버 상태 |
| GET | `/api/hello` | - | 첫 API |
| GET | `/api/notices` | - | 공고 목록 |
| POST | `/api/auth/signup` | - | 회원가입 `{username, password, nickname}` |
| POST | `/api/auth/login` | - | 로그인 → `accessToken` |
| GET | `/api/me` | Bearer 토큰 | 내 정보 |

## 폴더 구조

```
src/main/java/com/cheongyak/lecture
├─ LectureServerApplication.java   서버 시작점 (main)
├─ hello/      HelloController       첫 API
├─ member/     Member(엔티티) · MemberRepository · MemberService · AuthController · dto/
├─ notice/     Notice · NoticeRepository · NoticeController
├─ auth/       JwtService · JwtProperties   토큰 발급
├─ config/     SecurityConfig (누가 어디 접근 가능) · DataInitializer (연습 데이터)
└─ common/     ApiException · GlobalExceptionHandler (오류를 JSON 으로)
src/main/resources/application.yml   설정 (local / supabase 프로필)
```
