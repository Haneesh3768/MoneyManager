package com.project.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.expensetracker.dto.CategoryDTO;
import com.project.expensetracker.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

	private final CategoryService categoryService;
	
	@PostMapping
	public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
		CategoryDTO savedCategory = categoryService.savCategory(categoryDTO);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
	}
}
