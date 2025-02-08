package com.expense_tracker.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.expense_tracker.application.dto.CategorySpendDto;
import com.expense_tracker.application.dto.CurrencyWiseSpend;
import com.expense_tracker.application.dto.MonthandCategoryDto;
import com.expense_tracker.application.dto.MonthlySpentDto;
import com.expense_tracker.application.dto.PayeeRanking;
import com.expense_tracker.application.dto.TotalSpendingDto;
import com.expense_tracker.application.entity.Expenses;


public interface ExpensesService {
	
	Long findUserIdbyEmail(String email);
	Expenses addExpense(Long userId,Expenses expense);
	Page<Expenses> getAllExpensesByUserId(Long userId, int page, int size, String sortBy, String sortDirection);
	Expenses updateByUserId(Long expenseId,Expenses updatedExpense);
	void deleteExpenseById(Long expenseId);
	void deleteMultipleExpenses(List<Long> expenseIds);
	List<CategorySpendDto> getCategoryWiseSpending(Long userId,String currency);
	List<Integer> getAvailableYears(Long userId);
	List<String> getAvailableCurrency(Long userId);
	List<MonthlySpentDto> getMonthlySpendByUser(Long userId,Integer year,String currency);
	List<MonthandCategoryDto> getMonthwiseCategorySpending(Long userId,Integer year,String currency);
	List<TotalSpendingDto> getTotalSpendByCategoryandMonth(Long userId,String monthName,Integer year,String currency);
	List<PayeeRanking> getPayeeRanking(Long userId,String currency);
	List<CurrencyWiseSpend> getSpendingByCurrency(Long userId,Integer year);
}
