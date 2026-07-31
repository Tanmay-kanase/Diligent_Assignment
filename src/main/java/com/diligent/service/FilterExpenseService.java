package com.diligent.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.diligent.model.Expense;
import com.diligent.repository.ExpenseRepository;

@Service
public class FilterExpenseService {

    private final ExpenseRepository expenseRepository;

    public FilterExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Filters expenses by exact category match (case-insensitive).
     */
    public List<Expense> filterByCategory(String category) {
        return expenseRepository.findAll().stream()
                .filter(e -> e.getCategory() != null
                        && e.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    /**
     * Searches expenses by a keyword found in the title (case-insensitive,
     * partial match) and/or an optional category filter.
     */
    public List<Expense> searchExpenses(String keyword, String category) {
        return expenseRepository.findAll().stream()
                .filter(e -> !StringUtils.hasText(keyword)
                        || (e.getTitle() != null
                            && e.getTitle().toLowerCase().contains(keyword.toLowerCase())))
                .filter(e -> !StringUtils.hasText(category)
                        || (e.getCategory() != null
                            && e.getCategory().equalsIgnoreCase(category)))
                .toList();
    }
}
