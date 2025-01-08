package com.expense_tracker.application.dto;

import java.math.BigDecimal;

public class MonthandCategoryDto {
    private int month;
    private String monthName;
    private int year;
    //private String currency;
    private String category;
    private BigDecimal totalAmount;

    public MonthandCategoryDto() {
    }
    
    public MonthandCategoryDto(int month, String monthName, int year, String category, BigDecimal totalAmount) {
        this.month = month;
        this.monthName = monthName;
        this.year = year;
        //this.currency = currency;
        this.category = category;
        this.totalAmount = totalAmount;
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    /*
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }*/
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
