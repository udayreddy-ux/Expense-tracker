package com.expense_tracker.application.Controller;

import com.expense_tracker.application.dto.CategorySpendDto;
import com.expense_tracker.application.dto.CurrencyWiseSpend;
import com.expense_tracker.application.dto.MonthandCategoryDto;
import com.expense_tracker.application.dto.MonthlySpentDto;
import com.expense_tracker.application.dto.TotalSpendingDto;
import com.expense_tracker.application.entity.Expenses;
import com.expense_tracker.application.entity.Users;
import com.expense_tracker.application.rest.ExpenseController;
import com.expense_tracker.application.service.ExpensesService;
import com.expense_tracker.application.utility.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static org.mockito.ArgumentMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ExpenseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExpensesService expensesService;
    
    @MockBean
    private JwtUtil jwtUtil; 

    @InjectMocks
    private ExpenseController expenseController;

    private Users testUser;
    private Expenses testExpense;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();

        // Initialize testUser before using it
        testUser = new Users();
        testUser.setId(1L);
        testUser.setEmail("testuser@example.com");
        
        testExpense = new Expenses();
        testExpense.setId(100L);
        testExpense.setUser(testUser);
        testExpense.setCategory("Food");
        testExpense.setPayee("Restaurant ABC");
        testExpense.setAmount(new BigDecimal("25.50"));
        testExpense.setCurrency("USD");
        testExpense.setDescription("Lunch");
        testExpense.setCreatedAt(LocalDateTime.now());
        testExpense.setModifiedAt(LocalDateTime.now());

        // Mock authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser.getEmail());

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }


    @Test
    void testAddExpense_Success() throws Exception {
      
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(testUser.getId());
        when(expensesService.addExpense(eq(testUser.getId()), any(Expenses.class))).thenReturn(testExpense);

        // Perform the POST request
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.category").value("Food"))
                .andExpect(jsonPath("$.payee").value("Restaurant ABC"))
                .andExpect(jsonPath("$.amount").value(25.50))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.description").value("Lunch"));
    }
    
    @Test
    void testAddExpense_InvalidUser() throws Exception {
        
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);
        
        // Perform the POST request
        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpense)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid User ID"));
        
                
    }
    
    @Test
    void testUpdateExpense_Success() throws Exception {
    
        Long expenseId = 1L;
        Expenses updatedExpense = testExpense;
        updatedExpense.setId(expenseId);
        updatedExpense.setCategory("Utilities");
        updatedExpense.setAmount(new BigDecimal("50.00"));
        updatedExpense.setCurrency("USD");

       
        when(expensesService.updateByUserId(eq(expenseId), any(Expenses.class))).thenReturn(updatedExpense);

        mockMvc.perform(put("/api/expenses/{expenseId}", expenseId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedExpense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Utilities"))
                .andExpect(jsonPath("$.amount").value(50.00))
                .andExpect(jsonPath("$.currency").value("USD"));

  
    }
    
    @Test
    void testDeleteExpense_Success() throws Exception {
       

        Long expenseId = 1L;
        doNothing().when(expensesService).deleteExpenseById(expenseId);

        mockMvc.perform(delete("/api/expenses/{expenseId}", expenseId))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense deleted successfully"));

        
    }

    @Test
    void testDeleteExpense_NotFound() throws Exception {
        Long expenseId = 99L;
        doThrow(new IllegalArgumentException("Details not found")).when(expensesService).deleteExpenseById(expenseId);

        mockMvc.perform(delete("/api/expenses/{expenseId}", expenseId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Details not found"));
    }
    
    @Test
    void testDeleteMultipleExpenses_Success() throws Exception {

        List<Long> expenseIds = List.of(1L, 2L, 3L);
        doNothing().when(expensesService).deleteMultipleExpenses(expenseIds);

        mockMvc.perform(delete("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseIds)))
                .andExpect(status().isOk())
                .andExpect(content().string("Expenses deleted successfully"));
    }
    
    @Test
    void testGetCategoryWiseSpending_Success() throws Exception {
        List<CategorySpendDto> spendingData = Arrays.asList(
                new CategorySpendDto("Food", 100.0, 50.0),
                new CategorySpendDto("Entertainment", 200.0, 100.0)
        );

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(1L);
        when(expensesService.getCategoryWiseSpending(anyLong(), anyString())).thenReturn(spendingData);

        mockMvc.perform(get("/api/expenses/category-spending")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].totalAmount").value(100.0))
                .andExpect(jsonPath("$[1].category").value("Entertainment"))
                .andExpect(jsonPath("$[1].totalAmount").value(200.0));
    }

    @Test
    void testGetCategoryWiseSpending_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/category-spending")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAvailableYears_Success() throws Exception {
        List<Integer> years = Arrays.asList(2023, 2024);

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(1L);
        when(expensesService.getAvailableYears(anyLong())).thenReturn(years);

        mockMvc.perform(get("/api/expenses/years")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(2023))
                .andExpect(jsonPath("$[1]").value(2024));
    }

    @Test
    void testGetAvailableYears_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/years")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAvailableCurrencies_Success() throws Exception {
        List<String> currencies = Arrays.asList("USD", "EUR");

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(1L);
        when(expensesService.getAvailableCurrency(anyLong())).thenReturn(currencies);

        mockMvc.perform(get("/api/expenses/currencies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("USD"))
                .andExpect(jsonPath("$[1]").value("EUR"));
    }

    @Test
    void testGetAvailableCurrencies_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/currencies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    
    @Test
    void testGetMonthlySpendByUser_Success() throws Exception {
        List<MonthlySpentDto> monthlySpend = Arrays.asList(
                new MonthlySpentDto(1,"January", 2024, new BigDecimal("500.00")),
                new MonthlySpentDto(2,"February", 2024,new BigDecimal("600.00"))
        );

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(testUser.getId());
        when(expensesService.getMonthlySpendByUser(testUser.getId(), 2024, "USD")).thenReturn(monthlySpend);

        mockMvc.perform(get("/api/expenses/monthly-spend")
                .param("year", "2024")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(500.0))
                .andExpect(jsonPath("$[1].month").value(2))
                .andExpect(jsonPath("$[1].totalAmount").value(600.0));
    }

    @Test
    void testGetMonthlySpendByUser_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/monthly-spend")
                .param("year", "2024")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMonthlyCurrencyWiseSpend_Success() throws Exception {
        List<CurrencyWiseSpend> currencyWiseSpend = Arrays.asList(
                new CurrencyWiseSpend(1,"January",2024,"USD", new BigDecimal("1200.00")),
                new CurrencyWiseSpend(1,"January",2024,"USD", new BigDecimal("900.00"))
        );

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(testUser.getId());
        when(expensesService.getSpendingByCurrency(testUser.getId(), 2024)).thenReturn(currencyWiseSpend);

        mockMvc.perform(get("/api/expenses/monthly-currency-spend")
                .param("year", "2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalAmount").value(1200.0))
                .andExpect(jsonPath("$[1].totalAmount").value(900.0));
    }

    @Test
    void testGetMonthlyCurrencyWiseSpend_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/monthly-currency-spend")
                .param("year", "2024")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMonthlyCategoryByUser_Success() throws Exception {
        List<MonthandCategoryDto> monthCategorySpend = Arrays.asList(
                new MonthandCategoryDto(1,"January", 2024,"Food", new BigDecimal("200.00")),
                new MonthandCategoryDto(1,"January", 2024, "Transport",new BigDecimal("150.00"))
        );

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(testUser.getId());
        when(expensesService.getMonthwiseCategorySpending(testUser.getId(), 2024, "USD")).thenReturn(monthCategorySpend);

        mockMvc.perform(get("/api/expenses/monthwisecategory")
                .param("year", "2024")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].monthName").value("January"))
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].totalAmount").value(200.0))
                .andExpect(jsonPath("$[1].category").value("Transport"))
                .andExpect(jsonPath("$[1].totalAmount").value(150.0));
    }

    @Test
    void testGetMonthlyCategoryByUser_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/monthwisecategory")
                .param("year", "2024")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTotalSpendByCategoryandMonth_Success() throws Exception {
        List<TotalSpendingDto> totalSpend = Arrays.asList(
                new TotalSpendingDto(new BigDecimal("300.00"),new BigDecimal("45.00"),"Food"),
                new TotalSpendingDto(new BigDecimal("100.00"),new BigDecimal("55.00"),"Transport")
        );

        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(testUser.getId());
        when(expensesService.getTotalSpendByCategoryandMonth(testUser.getId(), "January", 2024, "USD")).thenReturn(totalSpend);

        mockMvc.perform(get("/api/expenses/averagesharebycategory")
                .param("monthName", "January")
                .param("year", "2024")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"))
                .andExpect(jsonPath("$[0].totalAmount").value(300.0))
                .andExpect(jsonPath("$[1].category").value("Transport"))
                .andExpect(jsonPath("$[1].totalAmount").value(100.0));
    }

    @Test
    void testGetTotalSpendByCategoryandMonth_UserNotFound() throws Exception {
        when(expensesService.findUserIdbyEmail(testUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/expenses/averagesharebycategory")
                .param("monthName", "January")
                .param("year", "2024")
                .param("currency", "USD")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
