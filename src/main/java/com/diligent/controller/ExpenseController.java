package com.diligent.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.diligent.dto.ExpenseRequest;
import com.diligent.model.Expense;
import com.diligent.service.CalculateService;
import com.diligent.service.ExpenseService;
import com.diligent.service.FilterExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final FilterExpenseService filterExpenseService;
    private final CalculateService calculateService;

    public ExpenseController(ExpenseService expenseService,
            FilterExpenseService filterExpenseService,
            CalculateService calculateService) {
        this.expenseService = expenseService;
        this.filterExpenseService = filterExpenseService;
        this.calculateService = calculateService;
    }

    // Add an expense
    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody ExpenseRequest request) {
        Expense created = expenseService.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // View all expenses
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    // Get a single expense by id
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    // Filter by category, and/or search by title keyword
    // e.g. GET /api/expenses/search?category=Food&keyword=pizza
    @GetMapping("/search")
    public ResponseEntity<List<Expense>> searchExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        List<Expense> result = filterExpenseService.searchExpenses(keyword, category);
        return ResponseEntity.ok(result);
    }

    // Strict filter by category only
    // e.g. GET /api/expenses/category/Food
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Expense>> filterByCategory(@PathVariable String category) {
        return ResponseEntity.ok(filterExpenseService.filterByCategory(category));
    }

    // Overall total
    @GetMapping("/total")
    public ResponseEntity<Double> getTotalExpenses() {
        return ResponseEntity.ok(calculateService.calculateTotal());
    }

    // Total grouped by category
    @GetMapping("/total/category")
    public ResponseEntity<Map<String, Double>> getTotalByCategory() {
        return ResponseEntity.ok(calculateService.calculateTotalByCategory());
    }

    // Monthly summary (yyyy-MM -> total)
    @GetMapping("/summary/monthly")
    public ResponseEntity<Map<String, Double>> getMonthlySummary() {
        return ResponseEntity.ok(calculateService.monthlySummary());
    }

    // Delete an expense
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
