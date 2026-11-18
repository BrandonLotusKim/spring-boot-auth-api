package com.cheongyak.lecture.config;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cheongyak.lecture.member.MemberRepository;
import com.cheongyak.lecture.member.MemberService;
import com.cheongyak.lecture.notice.Notice;
import com.cheongyak.lecture.notice.NoticeRepository;

// 서버가 켜질 때 한 번 실행: 테이블이 비어 있으면 연습용 데이터를 넣는다
@Component
public class DataInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final MemberRepository memberRepository;
	private final MemberService memberService;
	private final NoticeRepository noticeRepository;

	public DataInitializer(MemberRepository memberRepository, MemberService memberService, NoticeRepository noticeRepository) {
		this.memberRepository = memberRepository;
		this.memberService = memberService;
		this.noticeRepository = noticeRepository;
	}

	@Override
	public void run(String... args) {
		if (!memberRepository.existsByUsername("admin")) {
			memberService.signup("admin", "1234", "관리자");
			log.info("연습용 계정 생성: admin / 1234");
		}
		if (noticeRepository.count() == 0) {
			noticeRepository.saveAll(List.of(
					new Notice("청라 아크원 푸르지오", "인천", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 4)),
					new Notice("화성 향남 롯데캐슬 시그니처", "경기", LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 4)),
					new Notice("트리븐센텀 2단지", "부산", LocalDate.of(2026, 10, 26), LocalDate.of(2026, 10, 28))));
			log.info("연습용 공고 3건 생성");
		}
	}

}
