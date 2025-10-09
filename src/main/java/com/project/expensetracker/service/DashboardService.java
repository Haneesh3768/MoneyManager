package com.project.expensetracker.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.expensetracker.dto.ExpenseDTO;
import com.project.expensetracker.dto.IncomeDTO;
import com.project.expensetracker.entity.ProfileEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

	private final IncomeService incomeService;
	private final ExpenseService expenseService;
	private final ProfileService profileService;
	
	public Map<String, Object> getDashboardData(){
		ProfileEntity profile =  profileService.getCurrentProfile();
		Map<String, Object> returnValue = new LinkedHashMap();
		List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurretnUser();
		List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurretnUser();
		
	}
}
