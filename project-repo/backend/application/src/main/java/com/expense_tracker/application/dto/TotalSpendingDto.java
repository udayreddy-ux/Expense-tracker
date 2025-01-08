package com.expense_tracker.application.dto;

import java.math.BigDecimal;

public class TotalSpendingDto {
	private BigDecimal totalAmount;
	private BigDecimal percentageShare;
	private String category;
	public TotalSpendingDto(BigDecimal totalAmount, BigDecimal percentageShare, String category) {
		this.totalAmount = totalAmount;
		this.percentageShare = percentageShare;
		this.category = category;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public BigDecimal getPercentageShare() {
		return percentageShare;
	}
	public String getCategory() {
		return category;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public void setPercentageShare(BigDecimal percentageShare) {
		this.percentageShare = percentageShare;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
}
