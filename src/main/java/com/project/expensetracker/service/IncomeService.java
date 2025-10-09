package com.project.expensetracker.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.expensetracker.dto.ExpenseDTO;
import com.project.expensetracker.dto.IncomeDTO;
import com.project.expensetracker.entity.CategoryEntity;
import com.project.expensetracker.entity.ExpenseEntity;
import com.project.expensetracker.entity.IncomeEntity;
import com.project.expensetracker.entity.ProfileEntity;
import com.project.expensetracker.repository.CategoryRepository;
import com.project.expensetracker.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

	private final CategoryRepository categoryRepository;
	private final IncomeRepository incomeRepository;
	private final ProfileService profileService;
	
	
	
	//adding a new income to a database
		public IncomeDTO addIncome(IncomeDTO dto) {
			ProfileEntity profile =  profileService.getCurrentProfile();
			CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
					.orElseThrow(() -> new RuntimeException("category not found"));
			IncomeEntity newIncome = toEntity(dto, profile, category);
			newIncome = incomeRepository.save(newIncome);
			return toDto(newIncome);
			
		}
		
		//retrieve all income for the current month/based on start and end date
		public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser(){
			ProfileEntity profile = profileService.getCurrentProfile();
			LocalDate now = LocalDate.now();
			LocalDate startDate = now.withDayOfMonth(1);
			LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
			List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate, endDate);
			return list.stream().map(this::toDto).toList();
			
		}
		
		
		//delete expenses by id for current user
		public void deleteIncome(Long incomeId) {
			ProfileEntity profile = profileService.getCurrentProfile();
			IncomeEntity entity = incomeRepository.findById(incomeId)
					.orElseThrow(() -> new RuntimeException("Income not found"));
			if(!entity.getProfile().getId().equals(profile.getId())) {
				throw new RuntimeException("Unauthorized to delete this income");
			}
			incomeRepository.delete(entity);
		}
	
	//helper methods
	private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile,CategoryEntity category) {
		return IncomeEntity.builder()
				.name(dto.getName())
				.icon(dto.getIcon())
				.amount(dto.getAmount())
				.date(dto.getDate())
				.profile(profile)
				.category(category)
				.build();
	}
	
	private IncomeDTO toDto(IncomeEntity entity) {
		return IncomeDTO.builder()
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
