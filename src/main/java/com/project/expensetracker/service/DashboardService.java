package com.project.expensetracker.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.project.expensetracker.dto.ExpenseDTO;
import com.project.expensetracker.dto.IncomeDTO;
import com.project.expensetracker.dto.RecentTransactionDTO;
import com.project.expensetracker.entity.ProfileEntity;

import lombok.RequiredArgsConstructor;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurretnUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurretnUser();

        List<RecentTransactionDTO> recentTransactions = concat(
                latestIncomes.stream().map(incomeDTO ->
                        RecentTransactionDTO.builder()
                                .id(incomeDTO.getId())
                                .profileId(profile.getId())
                                .icon(incomeDTO.getIcon())
                                .name(incomeDTO.getName())
                                .amount(incomeDTO.getAmount())
                                .date(incomeDTO.getDate())
                                .createdAt(incomeDTO.getCreatedAt())
                                .updatedAt(incomeDTO.getUpdatedAt())
                                .type("income")
                                .build()
                ),
                latestExpenses.stream().map(expenseDTO ->
                        RecentTransactionDTO.builder()
                                .id(expenseDTO.getId())
                                .profileId(profile.getId())
                                .icon(expenseDTO.getIcon())
                                .name(expenseDTO.getName())
                                .amount(expenseDTO.getAmount())
                                .date(expenseDTO.getDate())
                                .createdAt(expenseDTO.getCreatedAt())
                                .updatedAt(expenseDTO.getUpdatedAt())
                                .type("expense")
                                .build()
                )
        ).filter(t -> t.getDate() != null)
        		.sorted((a, b) -> {
        		    int cmp = b.getDate().compareTo(a.getDate());
        		    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
        		        return b.getCreatedAt().compareTo(a.getCreatedAt());
        		    }
        		    return cmp;
        }).collect(Collectors.toList());

        returnValue.put("totalBalance",
                incomeService.getTotalIncomeForCurrentUser()
                        .subtract(expenseService.getTotalExpenseForCurrentUser())
        );
        returnValue.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("totalExpenses", expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }
}
