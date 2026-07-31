package com.diligent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.diligent.dto.ExpenseRequest;
import com.diligent.exception.ExpenseNotFoundException;
import com.diligent.model.Expense;
import com.diligent.repository.ExpenseRepository;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository);
    }

    private ExpenseRequest sampleRequest() {
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Coffee");
        request.setAmount(5.0);
        request.setCategory("Food");
        request.setDate(LocalDate.of(2026, 7, 1));
        return request;
    }

    @Test
    void addExpense_mapsRequestToExpense_andSaves() {
        ExpenseRequest request = sampleRequest();
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense e = invocation.getArgument(0);
            e.setId(1L);
            return e;
        });

        Expense result = expenseService.addExpense(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Coffee");
        assertThat(result.getAmount()).isEqualTo(5.0);
        assertThat(result.getCategory()).isEqualTo("Food");
        assertThat(result.getDate()).isEqualTo(LocalDate.of(2026, 7, 1));
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void getAllExpenses_delegatesToRepository() {
        Expense e = new Expense(1L, "Coffee", 5.0, "Food", LocalDate.now());
        when(expenseRepository.findAll()).thenReturn(List.of(e));

        List<Expense> result = expenseService.getAllExpenses();

        assertThat(result).containsExactly(e);
    }

    @Test
    void getExpenseById_returnsExpense_whenFound() {
        Expense e = new Expense(1L, "Coffee", 5.0, "Food", LocalDate.now());
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(e));

        Expense result = expenseService.getExpenseById(1L);

        assertThat(result).isEqualTo(e);
    }

    @Test
    void getExpenseById_throws_whenNotFound() {
        when(expenseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.getExpenseById(99L))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void deleteExpense_succeeds_whenExpenseExists() {
        when(expenseRepository.deleteById(1L)).thenReturn(true);

        expenseService.deleteExpense(1L);

        verify(expenseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteExpense_throws_whenExpenseDoesNotExist() {
        when(expenseRepository.deleteById(eq(99L))).thenReturn(false);

        assertThatThrownBy(() -> expenseService.deleteExpense(99L))
                .isInstanceOf(ExpenseNotFoundException.class);

        verify(expenseRepository, never()).save(any());
    }
}
