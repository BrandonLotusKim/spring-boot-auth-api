package com.cheongyak.lecture.notice;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// 청약 공고 테이블(notices). 실제 서비스에서는 수집기가 채운다
@Entity
@Table(name = "notices")
public class Notice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 20)
	private String region;

	private LocalDate receiptStart;

	private LocalDate receiptEnd;

	protected Notice() {
	}

	public Notice(String title, String region, LocalDate receiptStart, LocalDate receiptEnd) {
		this.title = title;
		this.region = region;
		this.receiptStart = receiptStart;
		this.receiptEnd = receiptEnd;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getRegion() {
		return region;
	}

	public LocalDate getReceiptStart() {
		return receiptStart;
	}

	public LocalDate getReceiptEnd() {
		return receiptEnd;
	}

}
