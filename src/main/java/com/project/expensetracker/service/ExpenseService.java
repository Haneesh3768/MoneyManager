package com.project.expensetracker.service;

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
