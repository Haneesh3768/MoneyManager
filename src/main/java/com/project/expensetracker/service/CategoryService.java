package com.project.expensetracker.service;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.expensetracker.dto.CategoryDTO;
import com.project.expensetracker.entity.CategoryEntity;
import com.project.expensetracker.entity.ProfileEntity;
import com.project.expensetracker.repostiory.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final ProfileService profileService;
	private final CategoryRepository categoryRepository;
	
	
	//save category
	public CategoryDTO savCategory(CategoryDTO categoryDTO) {
		ProfileEntity profile = profileService.getCurrentProfile();
		if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(),profile.getId() )) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,"Category with this name already there");
		}
		CategoryEntity newCategory = toEntity(categoryDTO, profile);
		newCategory = categoryRepository.save(newCategory);
		return toDTO(newCategory);
	}
	
	
	//helper methods
	private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
		return CategoryEntity.builder()
				.name(categoryDTO.getName())
				.icon(categoryDTO.getIcon())
				.profile(profile)
				.type(categoryDTO.getType())
				.build();
	}
	
	private CategoryDTO toDTO(CategoryEntity entity) {
		return CategoryDTO.builder()
				.id(entity.getId())
				.profileId(entity.getProfile()!= null ? entity.getProfile().getId() : null)
				.name(entity.getName())
				.icon(entity.getIcon())
				.createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt())
				.type(entity.getType())
				.build();
				
	}
}
