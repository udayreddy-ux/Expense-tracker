package com.expense_tracker.application.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.expense_tracker.application.dao.ExpenseRepository;
import com.expense_tracker.application.dao.UserRepository;
import com.expense_tracker.application.entity.Expenses;
import com.expense_tracker.application.entity.Users;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner{
	
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final boolean flag=false;
    public DataSeeder(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }
    @Value("${seed.data.enabled:false}")
	private boolean seedDataEnabled;
	private static final Long TARGET_USER_ID=(long) 9;
	private static final List<String> CURRENCIES=Arrays.asList("USD", "EUR", "GBP", "INR", "JPY", "AUD", "CAD", "CNY", "CHF");
	private static final Random RANDOM =new Random();
	private static final List<String> CATEGORIES=Arrays.asList("Food & Dining","Transportation","Utilities","Shopping","Healthcare","Education","Entertainment","Travel","Miscellaneous");
    	
	@Override
	public void run(String... args) throws Exception 
	{
		// TODO Auto-generated method stub
		if(flag==true) {
			Users user=userRepository.findById(TARGET_USER_ID).orElseThrow(() -> new IllegalArgumentException("User with ID " + TARGET_USER_ID + " not found"));
			if(!expenseRepository.findByUserId(TARGET_USER_ID, null).isEmpty()) 
			{
				for(int i=1;i<=50;i++) {
					expenseRepository.save(new Expenses(
							user,
							getRandomCategory(),
							"Payee " + (i+1),
							BigDecimal.valueOf(Math.round(RANDOM.nextDouble() * 500 * 100.0) / 100.0),
							getRandomCurrency(),
							"My expense " + i,
							getRandomTimestamp(2022,2025),
							getRandomTimestamp(2022,2025)
					));
				}
				System.out.println("Test data seeded successfully for user_id: "+TARGET_USER_ID);
			}
			else {
				System.out.println("User not found: "+TARGET_USER_ID);
			}
		}
	}

	private String getRandomCurrency() {
		// TODO Auto-generated method stub
		return CURRENCIES.get(RANDOM.nextInt(CURRENCIES.size()));
	}
	
	private String getRandomCategory() {
		return CATEGORIES.get(RANDOM.nextInt(CATEGORIES.size()));
	}

	private LocalDateTime getRandomTimestamp(int startYear, int endYear) {
		// TODO Auto-generated method stub
		int year=RANDOM.nextInt(endYear-startYear+1)+startYear;
		int month=RANDOM.nextInt(12)+1;
		int day=RANDOM.nextInt(28)+1;
		int hour=RANDOM.nextInt(24);
		int minute=RANDOM.nextInt(60);
		int second=RANDOM.nextInt(60);
		if (LocalDateTime.of(year, month, day, hour, minute, second).isAfter(LocalDateTime.now())) {
		    return getRandomTimestamp(startYear, endYear);
		}

		return LocalDateTime.of(year, month, day, hour, minute, second);
	}
	
}
