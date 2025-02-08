package com.expense_tracker.application.dto;

import java.math.BigDecimal;

public class CurrencyWiseSpend {
	
 	private Integer month;
    private String monthName;
    private Integer year;
    private String currency;
    private BigDecimal totalAmount;
	public CurrencyWiseSpend(Integer month, String monthName, Integer year, String currency, BigDecimal totalAmount) {
		this.month = month;
		this.monthName = monthName;
		this.year = year;
		this.currency = currency;
		this.totalAmount = totalAmount;
	}
	public Integer getMonth() {
		return month;
	}
	public String getMonthName() {
		return monthName;
	}
	public Integer getYear() {
		return year;
	}
	public String getCurrency() {
		return currency;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
    
	
}
