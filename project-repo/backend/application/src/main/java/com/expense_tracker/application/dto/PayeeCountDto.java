package com.expense_tracker.application.dto;

public class PayeeCountDto {
	
	private String payee;
	private Long totalCount;
	public PayeeCountDto(String payee, Long totalCount) {
		super();
		this.payee = payee;
		this.totalCount = totalCount;
	}
	public String getPayee() {
		return payee;
	}
	public Long getTotalCount() {
		return totalCount;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	
}
