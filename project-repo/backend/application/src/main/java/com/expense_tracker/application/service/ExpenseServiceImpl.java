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
import com.expense_tracker.application.dto.CurrencyWiseSpend;
import com.expense_tracker.application.dto.ExpenseDto;
import com.expense_tracker.application.dto.MonthandCategoryDto;
import com.expense_tracker.application.dto.MonthlySpentDto;
import com.expense_tracker.application.dto.PayeeCountDto;
import com.expense_tracker.application.dto.PayeeRanking;
import com.expense_tracker.application.dto.TotalAmountSpending;
import com.expense_tracker.application.dto.TotalSpendingDto;
import com.expense_tracker.application.dto.TotalSpent;
import com.expense_tracker.application.entity.Expenses;
import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.utility.OTPUtil;


@Service
public class ExpenseServiceImpl implements ExpensesService{
	
	@Autowired
	private ExpenseRepository expenseRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OTPService otpService;
	
	@Autowired
	private OTPUtil otpGenerator;
	
	@Autowired
	private EmailService emailService;
	
	@Override
	public Expenses addExpense(Long userId,Expenses expense) {
	    Users user = userRepository.findById(userId)
	            .orElseThrow(() -> new IllegalArgumentException("User not found"));

	    if (expense.getAmount() == null || expense.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
	        throw new IllegalArgumentException("Amount must be greater than 0");
	    }

	    expense.setUser(user);
	    return expenseRepository.save(expense);
	}
	
	@Override
	public Long findUserIdbyEmail(String email) {
		Users user=userRepository.findByEmail(email);
		if(user==null) {
			 throw new IllegalArgumentException("User not found");
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
	

	@Override
	public List<CurrencyWiseSpend> getSpendingByCurrency(Long userId, Integer year) {
		// TODO Auto-generated method stub
		return expenseRepository.getMonthlySpendingByCurrency(userId, year);
	}

	@Override
	public List<TotalAmountSpending> getTotalAmountSpent(Long userId, String currency) {
		// TODO Auto-generated method stub
		return expenseRepository.getTotalAmountSpent(userId, currency);
	}

	@Override
	public List<ExpenseDto> getRecentTransactions(Long userId, String currency) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(0, 9, Sort.by("createdAt").descending());
		return expenseRepository.getRecentTransactions(userId, currency,pageable);
	}

	@Override
	public List<PayeeCountDto> getPayees(Long userId, String currency) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(0, 1);
		return expenseRepository.getPayeeCount(userId, currency, pageable);
	}

	@Override
	public List<TotalSpent> getTotalSpentAmount(Long userId, String currency) {
		// TODO Auto-generated method stub
		return expenseRepository.getTotalSpent(userId, currency);
	}
	
	public void handleProfileChanges(String email) {
        String otp = otpGenerator.generateOtp();
        otpService.saveOTP(email, otp);
		emailService.sendEmail(email, "Password Reset Request",
			    "Dear User,\n\n" +
			    	    "You have requested to edit your personal details on your Expense Sage account.\n\n" +
			    	    "Your OTP is: "+otp + "\n\n" +
			    	    "Please enter this OTP within 3 attempts. If you fail to verify, you'll need to request a new OTP.\n\n" +
			    	    "Thank you,\n" +
			    	    "Team Expense Sage");
	}
	
}
