package com.expense_tracker.application.dto;

public class CategorySpendDto {

	private String category; // The name of the spending category
    private Double totalAmount; // The total spending amount for this category
    //private String currency; // The currency type (e.g., USD, EUR)
    private Double percentageShare; // The percentage of total spending in this currency for this category

    // Constructor
    public CategorySpendDto(String category, Double totalAmount, Double percentageShare) {
        this.category = category;
        this.totalAmount = totalAmount;
        //this.currency = currency;
        this.percentageShare = percentageShare;
    }

	public String getCategory() {
		return category;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}
	/*
	public String getCurrency() {
		return currency;
	}*/

	public Double getPercentageShare() {
		return percentageShare;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
	/*
	public void setCurrency(String currency) {
		this.currency = currency;
	}*/

	public void setPercentageShare(Double percentageShare) {
		this.percentageShare = percentageShare;
	}
}
