package com.diligent.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.diligent.model.Expense;

class ExpenseRepositoryTest {

    private ExpenseRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ExpenseRepository();
    }

    private Expense sampleExpense(String title, double amount, String category, LocalDate date) {
        Expense e = new Expense();
        e.setTitle(title);
        e.setAmount(amount);
        e.setCategory(category);
        e.setDate(date);
        return e;
    }

    @Test
    void save_assignsAutoIncrementingId() {
        Expense first = repository.save(sampleExpense("Coffee", 5.0, "Food", LocalDate.now()));
        Expense second = repository.save(sampleExpense("Bus ticket", 2.5, "Travel", LocalDate.now()));

        assertThat(first.getId()).isEqualTo(1L);
        assertThat(second.getId()).isEqualTo(2L);
    }

    @Test
    void findAll_returnsAllSavedExpenses() {
        repository.save(sampleExpense("Coffee", 5.0, "Food", LocalDate.now()));
        repository.save(sampleExpense("Bus ticket", 2.5, "Travel", LocalDate.now()));

        assertThat(repository.findAll()).hasSize(2);
    }

    @Test
    void findById_returnsExpense_whenPresent() {
        Expense saved = repository.save(sampleExpense("Coffee", 5.0, "Food", LocalDate.now()));

        Optional<Expense> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Coffee");
    }

    @Test
    void findById_returnsEmpty_whenAbsent() {
        Optional<Expense> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_removesExpense_andReturnsTrue() {
        Expense saved = repository.save(sampleExpense("Coffee", 5.0, "Food", LocalDate.now()));

        boolean removed = repository.deleteById(saved.getId());

        assertThat(removed).isTrue();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void deleteById_returnsFalse_whenIdNotFound() {
        boolean removed = repository.deleteById(999L);

        assertThat(removed).isFalse();
    }
}
