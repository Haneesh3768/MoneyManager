package com.project.expensetracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.expensetracker.dto.ExpenseDTO;
import com.project.expensetracker.entity.CategoryEntity;
import com.project.expensetracker.entity.ExpenseEntity;
import com.project.expensetracker.entity.ProfileEntity;
import com.project.expensetracker.repository.CategoryRepository;
import com.project.expensetracker.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {

	private final CategoryRepository categoryRepository;
	private final ExpenseRepository expenseRepository;
	private final ProfileService profileService;
	
	//adding a new expense to a database
	public ExpenseDTO addExpense(ExpenseDTO dto) {
		ProfileEntity profile =  profileService.getCurrentProfile();
		CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("category not found"));
		ExpenseEntity newExpense = toEntity(dto, profile, category);
		newExpense = expenseRepository.save(newExpense);
		return toDto(newExpense);
		
	}
	
	//retrieve all expenses for the current month/based on start and end date
	public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
		ProfileEntity profile = profileService.getCurrentProfile();
		LocalDate now = LocalDate.now();
		LocalDate startDate = now.withDayOfMonth(1);
		LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
		List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate, endDate);
		return list.stream().map(this::toDto).toList();
		
	}
	
	//delete expenses by id for current user
	public void deleteExpenses(Long expnseId) {
		ProfileEntity profile = profileService.getCurrentProfile();
		ExpenseEntity entity = expenseRepository.findById(expnseId)
				.orElseThrow(() -> new RuntimeException("Expense not found"));
		if(!entity.getProfile().getId().equals(profile.getId())) {
			throw new RuntimeException("Unauthorized to delete this expense");
		}
		expenseRepository.delete(entity);
	}
	
	//get latest 5 expenses for the current user 
	public List<ExpenseDTO> getLatest5ExpensesForCurretnUser(){
		ProfileEntity profile = profileService.getCurrentProfile();
		List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
		return list.stream().map(this::toDto).toList();
	}
	
	//get the total expenses for current user 
	public BigDecimal getTotalExpenseForCurrentUser() {
		ProfileEntity profile = profileService.getCurrentProfile();
		BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
		return total != null ? total: BigDecimal.ZERO;
	}
	
	//helper methods
	private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile,CategoryEntity category) {
		return ExpenseEntity.builder()
				.name(dto.getName())
				.icon(dto.getIcon())
				.amount(dto.getAmount())
				.date(dto.getDate())
				.profile(profile)
				.category(category)
				.build();
	}
	
	private ExpenseDTO toDto(ExpenseEntity entity) {
		return ExpenseDTO.builder()
		.id(entity.getId())
		.name(entity.getName())
		.icon(entity.getIcon())
		.categoryId(entity.getCategory()!= null ? entity.getCategory().getId():null)
		.categortName(entity.getCategory()!= null ? entity.getCategory().getName():"N/A")
		.amount(entity.getAmount())
		.date(entity.getDate())
		.createdAt(entity.getCreatedAt())
		.updatedAt(entity.getUpdatedAt())
		.build();
	}
}
