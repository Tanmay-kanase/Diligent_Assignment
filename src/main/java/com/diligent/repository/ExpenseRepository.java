package com.diligent.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.diligent.model.Expense;

/**
 * In-memory repository backed by a List. No database is used.
 * CopyOnWriteArrayList is used to keep it simple-thread-safe for
 * concurrent requests in a demo/no-DB setup.
 */
@Repository
public class ExpenseRepository {

    private final List<Expense> expenses = new CopyOnWriteArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(0);

    public Expense save(Expense expense) {
        expense.setId(idCounter.incrementAndGet());
        expenses.add(expense);
        return expense;
    }

    public List<Expense> findAll() {
        return expenses;
    }

    public Optional<Expense> findById(Long id) {
        return expenses.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public boolean deleteById(Long id) {
        return expenses.removeIf(e -> e.getId().equals(id));
    }
}
