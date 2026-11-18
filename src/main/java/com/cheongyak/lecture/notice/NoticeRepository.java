package com.cheongyak.lecture.notice;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	// 접수 시작일 빠른 순
	List<Notice> findAllByOrderByReceiptStartAsc();

}
