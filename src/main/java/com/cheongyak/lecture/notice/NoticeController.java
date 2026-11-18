package com.cheongyak.lecture.notice;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 공고 목록: GET /api/notices (로그인 없이 조회 가능)
@RestController
public class NoticeController {

	private final NoticeRepository noticeRepository;

	public NoticeController(NoticeRepository noticeRepository) {
		this.noticeRepository = noticeRepository;
	}

	@GetMapping("/api/notices")
	public List<NoticeResponse> list() {
		return noticeRepository.findAllByOrderByReceiptStartAsc().stream()
				.map(NoticeResponse::from)
				.toList();
	}

	public record NoticeResponse(Long id, String title, String region, LocalDate receiptStart, LocalDate receiptEnd) {
		static NoticeResponse from(Notice n) {
			return new NoticeResponse(n.getId(), n.getTitle(), n.getRegion(), n.getReceiptStart(), n.getReceiptEnd());
		}
	}

}
