package com.diligent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.diligent.dto.ExpenseRequest;
import com.diligent.exception.ExpenseNotFoundException;
import com.diligent.model.Expense;
import com.diligent.repository.ExpenseRepository;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense addExpense(ExpenseRequest request) {
        Expense expense = new Expense();
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    public void deleteExpense(Long id) {
        boolean removed = expenseRepository.deleteById(id);
        if (!removed) {
            throw new ExpenseNotFoundException(id);
        }
    }
}
