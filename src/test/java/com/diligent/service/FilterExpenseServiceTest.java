package com.diligent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diligent.model.Expense;
import com.diligent.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class FilterExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private FilterExpenseService filterExpenseService;

    private Expense coffee;
    private Expense busTicket;
    private Expense groceries;

    @BeforeEach
    void setUp() {
        filterExpenseService = new FilterExpenseService(expenseRepository);

        coffee = new Expense(1L, "Morning Coffee", 5.0, "Food", LocalDate.of(2026, 7, 1));
        busTicket = new Expense(2L, "Bus ticket", 2.5, "Travel", LocalDate.of(2026, 7, 2));
        groceries = new Expense(3L, "Weekly groceries", 45.0, "Food", LocalDate.of(2026, 7, 3));

        when(expenseRepository.findAll()).thenReturn(List.of(coffee, busTicket, groceries));
    }

    @Test
    void filterByCategory_returnsMatchingExpenses_caseInsensitive() {
        List<Expense> result = filterExpenseService.filterByCategory("food");

        assertThat(result).containsExactlyInAnyOrder(coffee, groceries);
    }

    @Test
    void filterByCategory_returnsEmpty_whenNoMatch() {
        List<Expense> result = filterExpenseService.filterByCategory("Rent");

        assertThat(result).isEmpty();
    }

    @Test
    void searchExpenses_matchesByTitleKeyword() {
        List<Expense> result = filterExpenseService.searchExpenses("coffee", null);

        assertThat(result).containsExactly(coffee);
    }

    @Test
    void searchExpenses_matchesByKeywordAndCategory() {
        List<Expense> result = filterExpenseService.searchExpenses("weekly", "Food");

        assertThat(result).containsExactly(groceries);
    }

    @Test
    void searchExpenses_returnsAll_whenNoFiltersProvided() {
        List<Expense> result = filterExpenseService.searchExpenses(null, null);

        assertThat(result).containsExactlyInAnyOrder(coffee, busTicket, groceries);
    }

    @Test
    void searchExpenses_returnsEmpty_whenKeywordMatchesNothing() {
        List<Expense> result = filterExpenseService.searchExpenses("nonexistent", null);

        assertThat(result).isEmpty();
    }
}
