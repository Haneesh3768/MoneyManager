package com.project.expensetracker.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Sort;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.expensetracker.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long>{

	//select * from expense_table where profile_id = ?1 order by date desc
	List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);
	
	List<ExpenseEntity> findTop5ByProfileIdOrderBYDateDesc(Long profileId);
	
	@Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id= :profileId")
	BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);
	
	List<ExpenseEntity> findByProfileIdandDateBetweenAnsNameComtainingIgnoreCase(
			Long profileId,
			LocalDate startDate,
			LocalDate endDate,
			String keyword,
			Sort sort
			);
	
	List<ExpenseEntity> LisfindByProfileIdAndDateBetween(Long profileId,LocalDate startDate,LocalDate endDate);
}
