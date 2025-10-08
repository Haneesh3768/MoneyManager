package com.project.expensetracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.expensetracker.dto.IncomeDTO;
import com.project.expensetracker.service.IncomeService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {

	private final IncomeService incomeService;
	
	@PostMapping
	public ResponseEntity<IncomeDTO> addExpense(@RequestBody IncomeDTO dto){
		IncomeDTO saved = incomeService.addIncome(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
}
