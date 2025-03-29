package com.expense_tracker.application.Service;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

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
import com.expense_tracker.application.service.ExpenseServiceImpl;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService; 

    private Users testUser;
    private Expenses testExpense;

    @BeforeEach
    void setUp() {
    	testUser = spy(new Users("John", "Doe", "john@example.com", "1234567890", "hashedPassword"));
        

        testExpense = new Expenses(
                testUser,
                "Food",
                "Restaurant ABC",
                new BigDecimal("20.50"),
                "USD",
                "Lunch at ABC Restaurant",
                LocalDateTime.of(2024, 3, 21, 12, 30),
                LocalDateTime.of(2024, 3, 21, 12, 30)
        );
    }

    @Test
    void testAddExpense_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(expenseRepository.save(any(Expenses.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Expenses savedExpense = expenseService.addExpense(1L, testExpense);

        // Verify
        verify(userRepository).findById(1L);
        verify(expenseRepository).save(testExpense);

        // Assert
        assertNotNull(savedExpense);
        assertEquals(testUser, savedExpense.getUser());
        assertEquals("Food", savedExpense.getCategory());
        assertEquals("Restaurant ABC", savedExpense.getPayee());
        assertEquals(new BigDecimal("20.50"), savedExpense.getAmount());
        assertEquals("USD", savedExpense.getCurrency());
    }

    @Test
    void testAddExpense_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.addExpense(1L, testExpense);
        });

        // Verify
        verify(userRepository).findById(1L);
        verify(expenseRepository, never()).save(any(Expenses.class));

        // Assertions
        assertEquals("User not found", exception.getMessage());
    }
    
    @Test
    void testAddExpenseInvalidAmount() {
        // Arrange
        Long userId = 1L;
        Users user = new Users("John", "Doe", "test@example.com", "1234567890", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Expenses invalidExpense = new Expenses();
        invalidExpense.setUser(user);
        invalidExpense.setCategory("Food");
        invalidExpense.setPayee("Restaurant");
        invalidExpense.setAmount(BigDecimal.ZERO); // 🚨 Invalid amount
        invalidExpense.setCurrency("USD");
        invalidExpense.setDescription("Dinner");
        invalidExpense.setCreatedAt(LocalDateTime.now());
        invalidExpense.setModifiedAt(LocalDateTime.now());

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.addExpense(userId, invalidExpense);
        });

        // Ensure the correct exception message
        assertEquals("Amount must be greater than 0", thrown.getMessage());

        // Ensure expense is never saved
        verify(expenseRepository, never()).save(any(Expenses.class));
    }



    @Test
    void testFindUserIdByEmail_Success() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(testUser);
        when(testUser.getId()).thenReturn(1L);
        
        // Act
        Long userId = expenseService.findUserIdbyEmail("john@example.com");

        // Assert
        assertNotNull(userId, "User ID should not be null");
        assertEquals(1L, userId);

        // Verify
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void testFindUserIdByEmail_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.findUserIdbyEmail("unknown@example.com");
        });

        // Verify
        verify(userRepository).findByEmail("unknown@example.com");

        // Assertions
        assertEquals("User not found", exception.getMessage());
    }
    
    @Test
    void testGetAllExpensesByUserId_Success() {
    	
    	Pageable pageable = PageRequest.of(0, 10, Sort.by("amount").ascending());
    	Page<Expenses> mockPage = new PageImpl<>(List.of(
    		       new Expenses(testUser, "Food", "Restaurant ABC", new java.math.BigDecimal("20.50"), 
                           "USD", "Lunch", java.time.LocalDateTime.now(), java.time.LocalDateTime.now())
    			));
    	when(expenseRepository.findByUserId(1L, pageable)).thenReturn(mockPage);
    	Page<Expenses> result = expenseService.getAllExpensesByUserId(1L, 0, 10, "amount", "asc");
    	
    	assertNotNull(result);
    	assertFalse(result.isEmpty());
    	assertEquals(1,result.getContent().size());
    	verify(expenseRepository).findByUserId(1L, pageable);
    }
    
    @Test
    void testGetAllExpensesByUserId_NoRecords() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("category").ascending());
        Page<Expenses> emptyPage = Page.empty();

        when(expenseRepository.findByUserId(1L, pageable)).thenReturn(emptyPage);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.getAllExpensesByUserId(1L, 0, 10, "category", "asc");
        });

        assertEquals("No records found for the user.", exception.getMessage());
        verify(expenseRepository).findByUserId(1L, pageable);
    }
    
    @Test
    public void testUpdateByUserId_Success() {
    	when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
    	when(expenseRepository.save(testExpense)).thenReturn(testExpense);

    	Expenses updatedExpense=new Expenses(
                testUser,
                "Food",
                "Restaurant ABD",
                new BigDecimal("20.52"),
                "USD",
                "Lunch at ABD Restaurant",
                LocalDateTime.of(2024, 3, 21, 12, 30),
                LocalDateTime.of(2024, 3, 22, 12, 30)
        );
    	Expenses result = expenseService.updateByUserId(1L,updatedExpense);
    	
    	assertNotNull(result);
    	assertEquals("Food",result.getCategory());
    	assertEquals("Restaurant ABD",result.getPayee());
    	assertEquals(new BigDecimal("20.52"),result.getAmount());
    	assertEquals("USD",result.getCurrency());
    	assertEquals("Lunch at ABD Restaurant",result.getDescription());
    	verify(expenseRepository).findById(1L);
    }
    
    @Test
    public void testUpdateByUserId_Invalidexpense() {
    	
    	when(expenseRepository.findById(1L)).thenReturn(Optional.empty());
    	
    	Exception exception=assertThrows(IllegalArgumentException.class,()->{
    		expenseService.updateByUserId(1L, testExpense);
    	});
    	assertEquals("Invalid entry or data not found!!",exception.getMessage());
    	verify(expenseRepository,never()).save(testExpense);
    }
    
    @Test
    void testDeleteExpenseById_Success() {
        // Arrange
        when(expenseRepository.existsById(1L)).thenReturn(true);

        // Act
        expenseService.deleteExpenseById(1L);

        // Assert & Verify
        verify(expenseRepository).existsById(1L);
        verify(expenseRepository).deleteById(1L);
    }

    @Test
    void testDeleteExpenseById_NotFound() {
        // Arrange
        when(expenseRepository.existsById(1L)).thenReturn(false);
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            expenseService.deleteExpenseById(1L);
        });
        // Assert
        assertEquals("Details not found", exception.getMessage());

        verify(expenseRepository, never()).deleteById(1L);
    }
    
    @Test
    void testDeleteMultipleExpenses_Success() {
        // Arrange
        List<Long> expenseIds = List.of(1L, 2L, 3L);

        // Act
        expenseService.deleteMultipleExpenses(expenseIds);

        // Assert & Verify
        verify(expenseRepository).deleteAllById(expenseIds);
    }

    @Test
    void testDeleteMultipleExpenses_EmptyList() {
        // Arrange
        List<Long> emptyList = List.of();

        // Act
        expenseService.deleteMultipleExpenses(emptyList);

        
        verify(expenseRepository).deleteAllById(emptyList);
    }

    @Test
    void testGetCategoryWiseSpending_Success() {
        // Arrange
        Long userId = 1L;
        String currency = "USD";
        List<CategorySpendDto> expectedList = List.of(
            new CategorySpendDto("Food", new Double("200.50"), new Double("30.5")),
            new CategorySpendDto("Transport", new Double("100.00"), new Double("15.0"))
        );

        when(expenseRepository.getCategoryWiseSpending(userId, currency)).thenReturn(expectedList);

        // Act
        List<CategorySpendDto> result = expenseService.getCategoryWiseSpending(userId, currency);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());
        assertEquals(new Double("200.50"), result.get(0).getTotalAmount());

        // Verify
        verify(expenseRepository).getCategoryWiseSpending(userId, currency);
    }

    @Test
    void testGetCategoryWiseSpending_EmptyResult() {
        // Arrange
        Long userId = 1L;
        String currency = "USD";

        when(expenseRepository.getCategoryWiseSpending(userId, currency)).thenReturn(List.of());

        // Act
        List<CategorySpendDto> result = expenseService.getCategoryWiseSpending(userId, currency);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getCategoryWiseSpending(userId, currency);
    }

    
    @Test
    void testGetAvailableYears_Success() {
        // Arrange
        List<Integer> expectedYears = List.of(2022, 2023, 2024);
        when(expenseRepository.getAvailableYears(1L)).thenReturn(expectedYears);

        // Act
        List<Integer> result = expenseService.getAvailableYears(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2022, result.get(0));

        // Verify
        verify(expenseRepository).getAvailableYears(1L);
    }

    @Test
    void testGetAvailableYears_Empty() {
        // Arrange
        when(expenseRepository.getAvailableYears(1L)).thenReturn(List.of());

        // Act
        List<Integer> result = expenseService.getAvailableYears(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getAvailableYears(1L);
    }

    @Test
    void testGetMonthlySpendByUser_Success() {
        // Arrange
        List<MonthlySpentDto> expectedData = List.of(
            new MonthlySpentDto(3, "March", 2024,new BigDecimal("500.00")),
            new MonthlySpentDto(4, "April", 2024, new BigDecimal("700.00"))
        );
        when(expenseRepository.getMonthlySpendByUserAndCurrency((long) 1, 2024, "USD")).thenReturn(expectedData);

        // Act
        List<MonthlySpentDto> result = expenseService.getMonthlySpendByUser((long) 1, 2024, "USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("March", result.get(0).getMonthName());

        // Verify
        verify(expenseRepository).getMonthlySpendByUserAndCurrency((long) 1, 2024, "USD");
    }

    @Test
    void testGetMonthlySpendByUser_Empty() {
        // Arrange
        when(expenseRepository.getMonthlySpendByUserAndCurrency((long) 1, 2024, "USD")).thenReturn(List.of());

        // Act
        List<MonthlySpentDto> result = expenseService.getMonthlySpendByUser((long) 1, 2024, "USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getMonthlySpendByUserAndCurrency((long) 1, 2024, "USD");
    }

    @Test
    void testGetAvailableCurrency_Success() {
        // Arrange
        List<String> expectedCurrencies = List.of("USD", "INR", "EUR");
        when(expenseRepository.getAvailableCurrerncies(1L)).thenReturn(expectedCurrencies);

        // Act
        List<String> result = expenseService.getAvailableCurrency(1L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("USD", result.get(0));

        // Verify
        verify(expenseRepository).getAvailableCurrerncies(1L);
    }

    @Test
    void testGetAvailableCurrency_Empty() {
        // Arrange
        when(expenseRepository.getAvailableCurrerncies(1L)).thenReturn(List.of());

        // Act
        List<String> result = expenseService.getAvailableCurrency(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getAvailableCurrerncies(1L);
    }

    @Test
    void testGetMonthwiseCategorySpending_Success() {
        // Arrange
        List<MonthandCategoryDto> expectedData = List.of(
            new MonthandCategoryDto(1,"March",2024,"Food", new BigDecimal("250.00")),
            new MonthandCategoryDto(2,"March",2024,"Transport", new BigDecimal("150.00"))
        );
        when(expenseRepository.getSpendingByCategoryAndMonthAndYear((long) 1, 2024, "USD")).thenReturn(expectedData);

        // Act
        List<MonthandCategoryDto> result = expenseService.getMonthwiseCategorySpending((long) 1, 2024, "USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());

        // Verify
        verify(expenseRepository).getSpendingByCategoryAndMonthAndYear((long) 1, 2024, "USD");
    }

    @Test
    void testGetMonthwiseCategorySpending_Empty() {
        // Arrange
        when(expenseRepository.getSpendingByCategoryAndMonthAndYear((long) 1, 2024, "USD")).thenReturn(List.of());

        // Act
        List<MonthandCategoryDto> result = expenseService.getMonthwiseCategorySpending((long) 1, 2024, "USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getSpendingByCategoryAndMonthAndYear((long) 1, 2024, "USD");
    }

    @Test
    void testGetTotalSpendByCategoryAndMonth_Success() {
        // Arrange
        List<TotalSpendingDto> expectedData = List.of(
            new TotalSpendingDto(new BigDecimal("250.00"), new BigDecimal("20.0"), "Food"),
            new TotalSpendingDto(new BigDecimal("100.00"), new BigDecimal("8.0"), "Travel")
        );
        when(expenseRepository.getTotalandAverageByCategoryAndMonth((long) 1, "March", 2024, "USD")).thenReturn(expectedData);

        // Act
        List<TotalSpendingDto> result = expenseService.getTotalSpendByCategoryandMonth((long) 1, "March", 2024, "USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());

        // Verify
        verify(expenseRepository).getTotalandAverageByCategoryAndMonth((long) 1, "March", 2024, "USD");
    }

    @Test
    void testGetTotalSpendByCategoryAndMonth_Empty() {
        // Arrange
        when(expenseRepository.getTotalandAverageByCategoryAndMonth((long) 1, "March", 2024, "USD")).thenReturn(List.of());

        // Act
        List<TotalSpendingDto> result = expenseService.getTotalSpendByCategoryandMonth((long) 1, "March", 2024, "USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getTotalandAverageByCategoryAndMonth((long) 1, "March", 2024, "USD");
    }
    
    @Test
    public void testGetSpendingByCurrency_Success() {
        // Arrange
        List<CurrencyWiseSpend> expectedData = List.of(
            new CurrencyWiseSpend(1,"March",2024,"USD",new BigDecimal("250.00")),
            new CurrencyWiseSpend(2,"March",2024,"USD",new BigDecimal("252.00"))
        );
        when(expenseRepository.getMonthlySpendingByCurrency((long) 1,2024)).thenReturn(expectedData);

        // Act
        List<CurrencyWiseSpend> result = expenseService.getSpendingByCurrency((long) 1,2024);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("252.00"), result.get(1).getTotalAmount());

        // Verify
        verify(expenseRepository).getMonthlySpendingByCurrency((long) 1,2024);
    }
    
    @Test
    public void testGetSpendingByCurrency_Empty() {
        // Arrange
        when(expenseRepository.getMonthlySpendingByCurrency((long) 1,2024)).thenReturn(List.of());

        // Act
        List<CurrencyWiseSpend> result = expenseService.getSpendingByCurrency((long) 1,2024);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getMonthlySpendingByCurrency((long) 1,2024);
    }
    
    
    @Test
    void testGetTotalAmountSpent_Success() {
        // Arrange
        List<TotalAmountSpending> expectedData = List.of(
            new TotalAmountSpending(new BigDecimal("1500.00"), 4L,1L),
            new TotalAmountSpending(new BigDecimal("500.00"), 3L,1L)
        );
        when(expenseRepository.getTotalAmountSpent((long) 1, "USD")).thenReturn(expectedData);

        // Act
        List<TotalAmountSpending> result = expenseService.getTotalAmountSpent((long) 1, "USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("1500.00"), result.get(0).getTotalAmount());

        // Verify
        verify(expenseRepository).getTotalAmountSpent((long) 1, "USD");
    }

    @Test
    void testGetTotalAmountSpent_Empty() {
        // Arrange
        when(expenseRepository.getTotalAmountSpent((long) 1, "USD")).thenReturn(List.of());

        // Act
        List<TotalAmountSpending> result = expenseService.getTotalAmountSpent((long) 1, "USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getTotalAmountSpent((long) 1, "USD");
    }

    @Test
    void testGetRecentTransactions_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 9, Sort.by("createdAt").descending());
        List<ExpenseDto> expectedData = List.of(
            new ExpenseDto("Restaurant ABC", new BigDecimal("50.00"), LocalDate.parse("2024-03-21")),
            new ExpenseDto("Uber", new BigDecimal("20.00"), LocalDate.parse("2024-03-21"))
        );

        when(expenseRepository.getRecentTransactions((long) 1,"USD",pageable)).thenReturn(expectedData); 

        // Act
        List<ExpenseDto> result = expenseService.getRecentTransactions((long) 1, "USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("50.00"), result.get(0).getAmount());
        assertEquals("Restaurant ABC", result.get(0).getPayee());

        // Verify
        verify(expenseRepository).getRecentTransactions((long) 1,"USD",pageable);
    }

    @Test
    void testGetRecentTransactions_Empty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 9, Sort.by("createdAt").descending());
        when(expenseRepository.getRecentTransactions((long) 1,"USD",pageable)).thenReturn(List.of());

        // Act
        List<ExpenseDto> result = expenseService.getRecentTransactions((long) 1, "USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getRecentTransactions((long) 1,"USD",pageable);
    }

    
    @Test
    void testGetPayees_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);
        List<PayeeCountDto> expectedData = List.of(
            new PayeeCountDto("Uber", 35L),
            new PayeeCountDto("Restaurant ABC",25L)
        );

        when(expenseRepository.getPayeeCount((long) 1, "USD", pageable)).thenReturn(expectedData);

        // Act
        List<PayeeCountDto> result = expenseService.getPayees((long) 1,"USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Uber", result.get(0).getPayee());

        // Verify
        verify(expenseRepository).getPayeeCount((long) 1,"USD", pageable);
    }

    @Test
    void testGetPayees_Empty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 1);
        when(expenseRepository.getPayeeCount((long) 1,"USD", pageable)).thenReturn(List.of());

        // Act
        List<PayeeCountDto> result = expenseService.getPayees((long) 1,"USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getPayeeCount((long) 1,"USD", pageable);
    }


    @Test
    void testGetTotalSpentAmount_Success() {
        // Arrange
        List<TotalSpent> expectedData = List.of(
            new TotalSpent(new BigDecimal("500.00"),"Food"),
            new TotalSpent(new BigDecimal("200.00"),"Transport")
        );
        when(expenseRepository.getTotalSpent((long) 1,"USD")).thenReturn(expectedData);

        // Act
        List<TotalSpent> result = expenseService.getTotalSpentAmount((long) 1,"USD");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getCategory());

        // Verify
        verify(expenseRepository).getTotalSpent((long) 1,"USD");
    }

    @Test
    void testGetTotalSpentAmount_Empty() {
        // Arrange
        when(expenseRepository.getTotalSpent((long) 1,"USD")).thenReturn(List.of());

        // Act
        List<TotalSpent> result = expenseService.getTotalSpentAmount((long) 1,"USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getTotalSpent((long) 1,"USD");
    }
    
    @Test
    void testGetPayeeRanking_Success() {
        // Arrange: Mocking database response
        List<Object[]> mockData = List.of(
            new Object[]{"Uber", new BigDecimal("250.00"), new BigDecimal("30.5"), 1},
            new Object[]{"Restaurant ABC", new BigDecimal("150.00"), new BigDecimal("18.0"), 2}
        );

        when(expenseRepository.getPayeeRankings((long) 1,"USD")).thenReturn(mockData);

        // Act: Call the service method
        List<PayeeRanking> result = expenseService.getPayeeRanking((long) 1,"USD");

        // Assert: Validate the transformation and data
        assertNotNull(result);
        assertEquals(2, result.size());

        // First Payee
        assertEquals("Uber", result.get(0).getPayee());
        assertEquals(new BigDecimal("250.00"), result.get(0).getTotalAmount());
        assertEquals(new BigDecimal("30.5"), result.get(0).getPercentageShare());
        assertEquals(1, result.get(0).getPayeerank());

        // Second Payee
        assertEquals("Restaurant ABC", result.get(1).getPayee());
        assertEquals(new BigDecimal("150.00"), result.get(1).getTotalAmount());
        assertEquals(new BigDecimal("18.0"), result.get(1).getPercentageShare());
        assertEquals(2, result.get(1).getPayeerank());

        // Verify
        verify(expenseRepository).getPayeeRankings((long) 1,"USD");
    }


    @Test
    void testGetPayeeRanking_Empty() {
        // Arrange
        when(expenseRepository.getPayeeRankings((long) 1,"USD")).thenReturn(List.of());

        // Act
        List<PayeeRanking> result = expenseService.getPayeeRanking((long) 1,"USD");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify
        verify(expenseRepository).getPayeeRankings((long) 1,"USD");
    }
}
