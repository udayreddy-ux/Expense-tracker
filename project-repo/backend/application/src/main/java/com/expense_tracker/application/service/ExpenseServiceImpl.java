package com.expense_tracker.application.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.expense_tracker.application.dao.ExpenseRepository;
import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.dto.CategorySpendDto;
import com.expense_tracker.application.dto.MonthandCategoryDto;
import com.expense_tracker.application.dto.MonthlySpentDto;
import com.expense_tracker.application.dto.PayeeRanking;
import com.expense_tracker.application.dto.TotalSpendingDto;
import com.expense_tracker.application.entity.Expenses;
import com.expense_tracker.application.entity.Users;


@Service
public class ExpenseServiceImpl implements ExpensesService{
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Expenses addExpense(Long userId,Expenses expense) {
		Users user=userRepository.findById(userId).orElseThrow(()->new IllegalArgumentException("User not found"));
		expense.setUser(user);
		return expenseRepository.save(expense);
	}
	
	@Override
	public Long findUserIdbyEmail(String email) {
		Users user=userRepository.findByEmail(email);
		if(user==null) {
			 new IllegalArgumentException("User not found");
		}
		return user.getId();
	}

	@Override
    public Page<Expenses> getAllExpensesByUserId(Long userId, int page, int size, String sortBy, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
        Page<Expenses> expenses = expenseRepository.findByUserId(userId, pageable);
        if (expenses.isEmpty()) {
            throw new IllegalArgumentException("No records found for the user.");
        }
        return expenses;
    }

	@Override
	public Expenses updateByUserId(Long expenseId, Expenses updatedExpense) {
		Expenses existingExpense=expenseRepository.findById(expenseId).orElse(null);
		if(existingExpense==null) {
			throw new IllegalArgumentException("Invalid entry or data not found!!");
		}
		existingExpense.setCategory(updatedExpense.getCategory());
		existingExpense.setPayee(updatedExpense.getPayee());
		existingExpense.setAmount(updatedExpense.getAmount());
		existingExpense.setCurrency(updatedExpense.getCurrency());
		existingExpense.setDescription(updatedExpense.getDescription());
		
		return expenseRepository.save(existingExpense);
	}

	@Override
	public void deleteExpenseById(Long expenseId) {
		if(!expenseRepository.existsById(expenseId)) {
			throw new IllegalArgumentException("Details not found");
		}
		expenseRepository.deleteById(expenseId);
	}

	@Override
	public void deleteMultipleExpenses(List<Long> expenseIds) {
		expenseRepository.deleteAllById(expenseIds);
	}
	
	@Override
	public List<CategorySpendDto> getCategoryWiseSpending(Long userId,String currency) {
		return expenseRepository.getCategoryWiseSpending(userId,currency);
	}
	
	@Override
	public List<Integer> getAvailableYears(Long userId){
		return expenseRepository.getAvailableYears(userId);
	}
	
	@Override
	 public List<MonthlySpentDto> getMonthlySpendByUser(Long userId,Integer year,String currency) {
        return expenseRepository.getMonthlySpendByUserAndCurrency(userId,year,currency);
    }

	@Override
	public List<String> getAvailableCurrency(Long userId) {
		// TODO Auto-generated method stub
		return expenseRepository.getAvailableCurrerncies(userId);
	}

	@Override
	public List<MonthandCategoryDto> getMonthwiseCategorySpending(Long userId, Integer year,String currency) {
		// TODO Auto-generated method stub
		return expenseRepository.getSpendingByCategoryAndMonthAndYear(userId, year, currency);
	}

	@Override
	public List<TotalSpendingDto> getTotalSpendByCategoryandMonth(Long userId, String monthName, Integer year,String currency) {
		// TODO Auto-generated method stub
		return expenseRepository.getTotalandAverageByCategoryAndMonth(userId, monthName, year, currency);
	}

	@Override
	public List<PayeeRanking> getPayeeRanking(Long userId, String currency) {
		// TODO Auto-generated method stub
		List<Object[]> results = expenseRepository.getPayeeRankings(userId, currency);
		return results.stream()
		        .map(row -> new PayeeRanking(
		            (String) row[0], // payee
		            (BigDecimal) row[1], // totalAmount
		            (BigDecimal) row[2], // percentageShare
		            ((Number) row[3]).intValue() // rankPay
		        ))
		        .toList();
	}
}
