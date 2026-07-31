package com.diligent.exception;

/**
 * Thrown when an expense with a given id does not exist.
 */
public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Expense not found with id: " + id);
    }

    public ExpenseNotFoundException(String message) {
        super(message);
    }
}
