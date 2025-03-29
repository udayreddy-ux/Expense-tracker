package com.expense_tracker.application.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="expenses")
public class Expenses {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id",nullable=false) //Foreign key
	@JsonIgnore
	private Users user;
	
	@Column(name="category",nullable=false)
	private String category;
	
	@Column(name="payee",nullable=false)
	private String payee;
	
	@NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
	private BigDecimal amount;
	
	@NotNull(message = "Currency is required")
	@Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid ISO 4217 code")
	@Column(name = "currency", nullable = false, length = 3)
	private String currency;
	
	@Column(name="description")
	private String description;
	
	@Column(name="created_at",nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
	
	@Column(name="modified_at",nullable=false)
	private LocalDateTime modifiedAt = LocalDateTime.now();
	
	//no-args constructors
	public Expenses() {
		
	}

	public Expenses(Users user, String category, String payee, BigDecimal amount, String currency, String description,
			LocalDateTime createdAt, LocalDateTime modifiedAt) {
		super();
		this.user = user;
		this.category = category;
		this.payee = payee;
		this.amount = amount;
		this.currency = currency;
		this.description = description;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public Long getId() {
		return id;
	}

	public Users getUser() {
		return user;
	}

	public String getCategory() {
		return category;
	}

	public String getPayee() {
		return payee;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public String getDescription() {
		return description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setModifiedAt(LocalDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
}
