package com.expense_tracker.application.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.expense_tracker.application.service.ExpensesService;
import com.expense_tracker.application.service.Userservice;

import jakarta.validation.Valid;

import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.dto.CategorySpendDto;
import com.expense_tracker.application.dto.MonthandCategoryDto;
import com.expense_tracker.application.dto.MonthlySpentDto;
import com.expense_tracker.application.dto.PayeeRanking;
import com.expense_tracker.application.dto.TotalSpendingDto;
import com.expense_tracker.application.entity.Expenses;
import com.expense_tracker.application.entity.Users;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
	
	@Autowired
	private ExpensesService expenseService;
	
	@Autowired
	private UserRepository userRepository;
	
	public ExpenseController(ExpensesService expenseService) {
		this.expenseService = expenseService;
	}

	@PostMapping
	public ResponseEntity<Expenses> addExpense(@Valid @RequestBody Expenses expense){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		
		 Expenses createdExpense = expenseService.addExpense(userId, expense);
		 return ResponseEntity.ok(createdExpense);
	}
	
	@GetMapping("/findUserIdByEmail")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Long> findUserIdByEmail(@RequestParam String email) {
        Long userId = expenseService.findUserIdbyEmail(email);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(userId);
    }
	
	@GetMapping("/{userId}")
    public ResponseEntity<?> getAllExpenses(
    		@PathVariable Long userId,
    	    @RequestParam(defaultValue = "0") int page,
    	    @RequestParam(defaultValue = "10") int size,
    	    @RequestParam(defaultValue = "createdAt") String sortBy,
    	    @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            Page<Expenses> expenses = expenseService.getAllExpensesByUserId(userId, page, size, sortBy, sortDirection);
            return ResponseEntity.ok(expenses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
	
	@PutMapping("/{expenseId}")
	public ResponseEntity<Expenses> updateExpenses(@Valid @PathVariable Long expenseId,@RequestBody Expenses updatedExpense){
		return ResponseEntity.ok(expenseService.updateByUserId(expenseId, updatedExpense));
	}
	
	@DeleteMapping("/{expenseId}")
	public ResponseEntity<?> deleteExpenses(@PathVariable Long expenseId){
		 try {
		        expenseService.deleteExpenseById(expenseId);
		        return ResponseEntity.ok("Expense deleted successfully");
		    } catch (IllegalArgumentException e) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		    }
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteMultipleExpenses(@RequestBody List<Long> expenseIds){
		expenseService.deleteMultipleExpenses(expenseIds);
		return ResponseEntity.ok("Expenses deleted successfully");
	}
	
	@GetMapping("/category-spending")
	@PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategorySpendDto>> getCategoryWiseSpending(@RequestParam String currency) {
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		List<CategorySpendDto> spendingData = expenseService.getCategoryWiseSpending(userId,currency);
        return ResponseEntity.ok(spendingData);
    }
	
	@GetMapping("/years")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<Integer>> getAvailableYears(){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		List<Integer> years=expenseService.getAvailableYears(userId);
		return ResponseEntity.ok(years);
	}
	
	@GetMapping("/currencies")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<String>> getAvailableCurrencies(){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} 
		List<String> curr=expenseService.getAvailableCurrency(userId);
		return ResponseEntity.ok(curr);
	}
	
	@GetMapping("/monthly-spend")
	@PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MonthlySpentDto>> getMonthlySpendByUser(@RequestParam Integer year,@RequestParam String currency) {
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
        List<MonthlySpentDto> monthlySpend = expenseService.getMonthlySpendByUser(userId,year, currency);
        return ResponseEntity.ok(monthlySpend);
    }
	
	@GetMapping("/monthwisecategory")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<MonthandCategoryDto>> getMonthlyCategoryByUser(@RequestParam Integer year,@RequestParam String currency){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		List<MonthandCategoryDto> monthwisecategory=expenseService.getMonthwiseCategorySpending(userId, year, currency);
		return ResponseEntity.ok(monthwisecategory);
	}
	
	@GetMapping("/averagesharebycategory")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<TotalSpendingDto>> getTotalSpendByCategoryandMonth(@RequestParam String monthName,@RequestParam Integer year,@RequestParam String currency){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		List<TotalSpendingDto> monthaveragecategory=expenseService.getTotalSpendByCategoryandMonth(userId, monthName, year, currency);
		return ResponseEntity.ok(monthaveragecategory);
	}
	
	@GetMapping("/payeeranks")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<List<PayeeRanking>> getPayeeRankings(@RequestParam String currency){
		String userEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Long userId=expenseService.findUserIdbyEmail(userEmail);
		if(userId==null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		List<PayeeRanking> payeewiseranking=expenseService.getPayeeRanking(userId, currency);
		return ResponseEntity.ok(payeewiseranking);
	}
}
