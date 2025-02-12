package com.expense_tracker.application.dto;


import java.math.BigDecimal;

public class TotalAmountSpending {

    private BigDecimal totalAmount;
    private Long numberOfTransactions;
    private Long totalPayees;

    public TotalAmountSpending(BigDecimal totalAmount, Long numberOfTransactions, Long totalPayees) {
        this.totalAmount = totalAmount;
        this.numberOfTransactions = numberOfTransactions;
        this.totalPayees = totalPayees;
    }

    // Getters
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Long getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public Long getTotalPayees() {
        return totalPayees;
    }

    // Setters
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setNumberOfTransactions(Long numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public void setTotalPayees(Long totalPayees) {
        this.totalPayees = totalPayees;
    }
}
