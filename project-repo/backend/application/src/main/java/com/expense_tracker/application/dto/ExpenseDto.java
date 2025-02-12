package com.expense_tracker.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDto {
    private String payee;
    private BigDecimal amount;
    private LocalDate createdAt;

    // Constructor
    public ExpenseDto(String payee, BigDecimal amount, LocalDate createdAt) {
        this.payee = payee;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
