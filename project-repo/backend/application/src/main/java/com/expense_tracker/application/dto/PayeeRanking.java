package com.expense_tracker.application.dto;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PayeeRanking {
	private String payee;
	private BigDecimal totalAmount;
	private BigDecimal percentageShare;
	private int payeerank;
	public PayeeRanking(String payee, BigDecimal totalAmount, BigDecimal percentageShare,int payeerank) {
		super();
		this.payee = payee;
		this.totalAmount = totalAmount;
		this.percentageShare = percentageShare;
		this.payeerank=payeerank;
	}
	public String getPayee() {
		return payee;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public BigDecimal getPercentageShare() {
		return percentageShare;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public void setPercentageShare(BigDecimal percentageShare) {
		this.percentageShare = percentageShare;
	}
	public int getPayeerank() {
		return payeerank;
	}
	public void setPayeerank(int payeerank) {
		this.payeerank = payeerank;
	}
}
