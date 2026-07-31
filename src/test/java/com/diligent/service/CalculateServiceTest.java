package com.diligent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diligent.model.Expense;
import com.diligent.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class CalculateServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private CalculateService calculateService;

    @BeforeEach
    void setUp() {
        calculateService = new CalculateService(expenseRepository);
    }

    @Test
    void calculateTotal_returnsZero_whenNoExpenses() {
        when(expenseRepository.findAll()).thenReturn(List.of());

        assertThat(calculateService.calculateTotal()).isEqualTo(0.0);
    }

    @Test
    void calculateTotal_sumsAllExpenseAmounts() {
        when(expenseRepository.findAll()).thenReturn(List.of(
                new Expense(1L, "Coffee", 5.0, "Food", LocalDate.of(2026, 7, 1)),
                new Expense(2L, "Bus ticket", 2.5, "Travel", LocalDate.of(2026, 7, 2)),
                new Expense(3L, "Groceries", 45.0, "Food", LocalDate.of(2026, 7, 3))
        ));

        assertThat(calculateService.calculateTotal()).isEqualTo(52.5);
    }

    @Test
    void calculateTotalByCategory_groupsAndSumsCorrectly() {
        when(expenseRepository.findAll()).thenReturn(List.of(
                new Expense(1L, "Coffee", 5.0, "Food", LocalDate.of(2026, 7, 1)),
                new Expense(2L, "Bus ticket", 2.5, "Travel", LocalDate.of(2026, 7, 2)),
                new Expense(3L, "Groceries", 45.0, "Food", LocalDate.of(2026, 7, 3))
        ));

        Map<String, Double> result = calculateService.calculateTotalByCategory();

        assertThat(result)
                .hasSize(2)
                .containsEntry("Food", 50.0)
                .containsEntry("Travel", 2.5);
    }

    @Test
    void monthlySummary_groupsByYearMonth_sortedChronologically() {
        when(expenseRepository.findAll()).thenReturn(List.of(
                new Expense(1L, "Coffee", 5.0, "Food", LocalDate.of(2026, 7, 1)),
                new Expense(2L, "Rent", 500.0, "Housing", LocalDate.of(2026, 6, 15)),
                new Expense(3L, "Groceries", 45.0, "Food", LocalDate.of(2026, 7, 20))
        ));

        Map<String, Double> result = calculateService.monthlySummary();

        assertThat(result.keySet()).containsExactly("2026-06", "2026-07");
        assertThat(result.get("2026-06")).isEqualTo(500.0);
        assertThat(result.get("2026-07")).isEqualTo(50.0);
    }

    @Test
    void monthlySummary_returnsEmptyMap_whenNoExpenses() {
        when(expenseRepository.findAll()).thenReturn(List.of());

        assertThat(calculateService.monthlySummary()).isEmpty();
    }
}
