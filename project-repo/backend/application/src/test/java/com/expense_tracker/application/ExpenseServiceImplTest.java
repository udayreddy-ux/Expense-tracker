package com.expense_tracker.application;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.expense_tracker.application.dao.ExpenseRepository;
import com.expense_tracker.application.entity.Expenses;
import com.expense_tracker.application.rest.ExpenseController;
import com.expense_tracker.application.service.ExpenseServiceImpl;
import com.expense_tracker.application.service.ExpensesService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.boot.test.context.SpringBootTest;
@SpringBootTest
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpensesService expensesService;  // Mocking the service

    @InjectMocks
    private ExpenseServiceImpl expenseServiceImpl;

    @InjectMocks
    private ExpenseController expenseController;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("udaysuryakdr@gmail.com", "Uday49869*");
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllExpensesByUserIdWithPagination() {
        Long userId = 1L;
        List<Expenses> expensesList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            expensesList.add(new Expenses(
                null,
                "Food & Dining",
                "Merchant " + i,
                BigDecimal.valueOf(100 + i),
                "USD",
                "Test description " + i,
                LocalDateTime.now(),
                LocalDateTime.now()
            ));
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Expenses> expensesPage = new PageImpl<>(expensesList.subList(0, 5), pageable, expensesList.size());

        when(expenseRepository.findByUserId(eq(userId), eq(pageable))).thenReturn(expensesPage);

        Page<Expenses> result = expenseServiceImpl.getAllExpensesByUserId(userId, 0, 5, "createdAt", "desc");

        assertNotNull(result, "Result should not be null");
        assertEquals(10, result.getTotalElements(), "Total elements mismatch");
        assertEquals(5, result.getContent().size(), "Page size mismatch");
        assertEquals("Merchant 0", result.getContent().get(0).getPayee(), "First record payee mismatch");

        verify(expenseRepository, times(1)).findByUserId(eq(userId), eq(pageable));
    }

    @Test
    void testAddExpenseInvalidAmount() {
        Expenses expense = new Expenses();
        expense.setAmount(BigDecimal.ZERO);
        expense.setCurrency("USD");
        expense.setPayee("Test Merchant");

        when(expensesService.addExpense(any(Long.class), any(Expenses.class)))
            .thenThrow(new IllegalArgumentException("Amount must be greater than zero"));

        Long userId = 1L;  // Add this userId

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseController.addExpense(expense);  // Pass userId
        });

        String expectedMessage = "Amount must be greater than zero";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

}
