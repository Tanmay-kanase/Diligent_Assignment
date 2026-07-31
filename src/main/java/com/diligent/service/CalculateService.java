package com.diligent.service;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.diligent.model.Expense;
import com.diligent.repository.ExpenseRepository;

@Service
public class CalculateService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final ExpenseRepository expenseRepository;

    public CalculateService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Overall total across all expenses.
     */
    public double calculateTotal() {
        return expenseRepository.findAll().stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    /**
     * Total amount grouped by category.
     */
    public Map<String, Double> calculateTotalByCategory() {
        return expenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));
    }

    /**
     * Monthly summary: total amount grouped by "yyyy-MM", sorted chronologically.
     */
    public Map<String, Double> monthlySummary() {
        return expenseRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().format(MONTH_FORMATTER),
                        Collectors.summingDouble(Expense::getAmount)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.naturalOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }
}
