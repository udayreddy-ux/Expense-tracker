package com.expense_tracker.application.dto;

import java.math.BigDecimal;

public class TotalSpent {

    private BigDecimal totalAmount;
    private String category;

    // Constructor for DTO projection in JPA
    public TotalSpent(BigDecimal totalAmount, String category) {
        this.totalAmount = totalAmount;
        this.category = category;
    }

    // Getters and Setters
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
