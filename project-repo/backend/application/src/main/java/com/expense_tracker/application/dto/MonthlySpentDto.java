package com.expense_tracker.application.dto;

import java.math.BigDecimal;

public class MonthlySpentDto {
	 	private int month;
	    private String monthName;
	    private int year;
	    //private String currency;
	    private BigDecimal totalAmount;

	    public MonthlySpentDto(int month, String monthName, int year, BigDecimal totalAmount) {
	        this.month = month;
	        this.monthName = monthName;
	        this.year=year;
	        this.totalAmount = totalAmount;
	    }

	    public int getYear() {
			return year;
		}

		public void setYear(int year) {
			this.year = year;
		}

		// Getters and Setters
	    public int getMonth() {
	        return month;
	    }

	    public void setMonth(int month) {
	        this.month = month;
	    }

	    public String getMonthName() {
	        return monthName;
	    }

	    public void setMonthName(String monthName) {
	        this.monthName = monthName;
	    }

	    public BigDecimal getTotalAmount() {
	        return totalAmount;
	    }

	    public void setTotalAmount(BigDecimal totalAmount) {
	        this.totalAmount = totalAmount;
	    }
	    /*
	    public String getCurrency() {
	        return currency;
	    }

	    public void setCurrency(String currency) {
	        this.currency = currency;
	    }*/
}
