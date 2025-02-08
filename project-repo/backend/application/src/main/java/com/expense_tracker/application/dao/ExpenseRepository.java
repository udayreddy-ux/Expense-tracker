package com.expense_tracker.application.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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

@Repository
public interface ExpenseRepository extends JpaRepository<Expenses,Long>{
	
	Page<Expenses> findByUserId(Long userId, Pageable pageable);
	/*
	@Query("SELECT new com.expense_tracker.application.dto.CategorySpendDto(" +
		       "e.category, " +
		       "CAST(SUM(e.amount) AS double) AS totalAmount, " +
		       "e.currency, " +
		       "CAST(SUM(e.amount) * 100.0 / (SELECT SUM(e2.amount) FROM Expenses e2 WHERE e2.user.id = :userId AND e2.currency = e.currency) AS double) AS percentageShare " +
		       ") " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "GROUP BY e.category, e.currency " +
		       "ORDER BY totalAmount DESC")*/
	@Query("SELECT new com.expense_tracker.application.dto.CategorySpendDto(" +
		       "e.category, " +
		       "CAST(SUM(e.amount) AS double) AS totalAmount, " +
		       "CAST(SUM(e.amount) * 100.0 / (SELECT SUM(e2.amount) FROM Expenses e2 WHERE e2.user.id = :userId AND e2.currency = :currency) AS double) AS percentageShare " +
		       ") " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "AND e.currency = :currency " +
		       "GROUP BY e.category " +
		       "ORDER BY totalAmount DESC")
	List<CategorySpendDto> getCategoryWiseSpending(Long userId,@Param("currency") String currency);
	
	@Query("SELECT DISTINCT EXTRACT(YEAR FROM e.createdAt) AS year " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "ORDER BY year ASC")
	List<Integer> getAvailableYears(Long userId);
	
	@Query("SELECT DISTINCT e.currency " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "ORDER BY currency ASC")
	List<String> getAvailableCurrerncies(Long userId);
	
	
	/*
	@Query("SELECT new com.expense_tracker.application.dto.MonthlySpentDto(" +
		       "EXTRACT(MONTH FROM e.createdAt) AS month, " +
		       "TO_CHAR(e.createdAt, 'Month') AS monthName, " +
		       "EXTRACT(YEAR FROM e.createdAt) AS year, " +
		       "e.currency, " +
		       "COALESCE(SUM(e.amount), 0)) " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId AND EXTRACT(YEAR FROM e.createdAt) = :year " +
		       "GROUP BY EXTRACT(MONTH FROM e.createdAt), TO_CHAR(e.createdAt, 'Month'), EXTRACT(YEAR FROM e.createdAt), e.currency " +
		       "ORDER BY month, e.currency")*/
	@Query("SELECT new com.expense_tracker.application.dto.MonthlySpentDto(" +
		       "EXTRACT(MONTH FROM e.createdAt) AS month, " +
		       "TO_CHAR(e.createdAt, 'Month') AS monthName, " +
		       "EXTRACT(YEAR FROM e.createdAt) AS year, " +
		       "COALESCE(SUM(e.amount), 0)) " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId AND EXTRACT(YEAR FROM e.createdAt) = :year AND e.currency= :currency " +
		       "GROUP BY EXTRACT(MONTH FROM e.createdAt), TO_CHAR(e.createdAt, 'Month'), EXTRACT(YEAR FROM e.createdAt) " +
		       "ORDER BY month")
	List<MonthlySpentDto> getMonthlySpendByUserAndCurrency(Long userId,@Param("year") Integer year,@Param("currency") String currency);
	
	@Query("SELECT new com.expense_tracker.application.dto.CurrencyWiseSpend(" +
			"EXTRACT(MONTH FROM e.createdAt) AS month, " +
			"TO_CHAR(e.createdAt,'Month') AS monthName, " +
			"EXTRACT(YEAR FROM e.createdAt) AS year, "+
			"e.currency ,"+
			"COALESCE(SUM(e.amount),0)) " +
			"FROM Expenses e " +
			"WHERE e.user.id = :userId AND EXTRACT(YEAR FROM e.createdAt) = :year " +
			"GROUP BY EXTRACT(MONTH FROM e.createdAt), TO_CHAR(e.createdAt,'Month'),EXTRACT(YEAR FROM e.createdAt),e.currency " +
			"ORDER BY month"
			)
	List<CurrencyWiseSpend> getMonthlySpendingByCurrency(Long userId,@Param("year") Integer year);
	
	/*
	@Query("SELECT new com.expense_tracker.application.dto.MonthandCategoryDto(" +
		       "EXTRACT(MONTH FROM e.createdAt) AS month, " +
		       "TO_CHAR(e.createdAt, 'Month') AS monthName, " +
		       "EXTRACT(YEAR FROM e.createdAt) AS year, " +
		       "e.currency, " +
		       "e.category, " +
		       "COALESCE(SUM(e.amount), 0)) " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "AND EXTRACT(YEAR FROM e.createdAt) = :year " +
		       "GROUP BY EXTRACT(MONTH FROM e.createdAt), " +
		       "TO_CHAR(e.createdAt, 'Month'), " +
		       "EXTRACT(YEAR FROM e.createdAt), " +
		       "e.category "+
		       "ORDER BY EXTRACT(MONTH FROM e.createdAt), e.currency")*/
	@Query("SELECT new com.expense_tracker.application.dto.MonthandCategoryDto(" +
		       "EXTRACT(MONTH FROM e.createdAt) AS month, " +
		       "TO_CHAR(e.createdAt, 'Month') AS monthName, " +
		       "EXTRACT(YEAR FROM e.createdAt) AS year, " +
		       "e.category, " +
		       "COALESCE(SUM(e.amount), 0)) " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "AND EXTRACT(YEAR FROM e.createdAt) = :year " +
		       "AND e.currency = :currency " + // Added space before GROUP BY
		       "GROUP BY EXTRACT(MONTH FROM e.createdAt), " +
		       "TO_CHAR(e.createdAt, 'Month'), " +
		       "EXTRACT(YEAR FROM e.createdAt), " +
		       "e.category " +
		       "ORDER BY EXTRACT(MONTH FROM e.createdAt)")
	List<MonthandCategoryDto> getSpendingByCategoryAndMonthAndYear(@Param("userId") Long userId, @Param("year") Integer year,@Param("currency") String currency);
	
	/*
	@Query("SELECT new com.expense_tracker.application.dto.TotalSpendingDto(" +
		       "CAST(COALESCE(SUM(e.amount), 0) AS BigDecimal), " +
		       "CAST((SUM(e.amount) * 100.0 / (SELECT SUM(e2.amount) FROM Expenses e2 WHERE e2.user.id = :userId AND e2.currency = e.currency)) AS BigDecimal), " +
		       "e.category) " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId " +
		       "AND TRIM(TO_CHAR(e.createdAt, 'Month')) = :monthName " +
		       "AND EXTRACT(YEAR FROM e.createdAt) = :year " +
		       "GROUP BY e.category, e.currency")*/
	@Query("SELECT new com.expense_tracker.application.dto.TotalSpendingDto(" +
		       "CAST(COALESCE(SUM(e.amount), 0) AS BigDecimal), " +
		       "CAST((SUM(e.amount) * 100.0 / (SELECT SUM(e2.amount) FROM Expenses e2 WHERE e2.user.id = :userId AND e2.currency = :currency)) AS BigDecimal), " +
		       "e.category) " +
		       "FROM Expenses e " +
		       "WHERE e.user.id = :userId AND e.currency= :currency " +
		       "AND TRIM(TO_CHAR(e.createdAt, 'Month')) = :monthName " +
		       "AND EXTRACT(YEAR FROM e.createdAt) = :year " +
		       "GROUP BY e.category")
	List<TotalSpendingDto> getTotalandAverageByCategoryAndMonth(@Param("userId") Long userId, @Param("monthName") String monthName,@Param("year") Integer year,@Param("currency") String currency);

	@Query(value = "SELECT e.payee, " +
            "SUM(e.amount) AS totalAmount, " +
            "ROUND(SUM(e.amount) * 100.0 / (SELECT SUM(e2.amount) FROM expenses e2 WHERE e2.user_id = :userId AND e2.currency = :currency), 2) AS percentageShare, " +
            "DENSE_RANK() OVER(ORDER BY SUM(e.amount) DESC) AS rankPay " +
            "FROM expenses e " +
            "WHERE e.user_id = :userId AND e.currency = :currency " +
            "GROUP BY e.payee",
    nativeQuery = true)
	List<Object[]> getPayeeRankings(@Param("userId") Long userId, @Param("currency") String currency);
}
