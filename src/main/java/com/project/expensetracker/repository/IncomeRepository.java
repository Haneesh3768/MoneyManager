package com.project.expensetracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.project.expensetracker.entity.IncomeEntity;

@Repository
public interface IncomeRepository extends JpaRepository<IncomeEntity, Long>{

	//select * from expense_table where profile_id = ?1 order by date desc
		List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);
		
		List<IncomeEntity> findTop5ByProfileIdOrderBYDateDesc(Long profileId);
		
		@Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id= :profileId")
		BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
		
		List<IncomeEntity> findByProfileIdandDateBetweenAnsNameComtainingIgnoreCase(
				Long profileId,
				LocalDate startDate,
				LocalDate endDate,
				String keyword,
				Sort sort
				);
		
		List<IncomeEntity> LisfindByProfileIdAndDateBetween(Long profileId,LocalDate startDate,LocalDate endDate);
}
