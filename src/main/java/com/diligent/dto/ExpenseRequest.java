package com.diligent.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Data;

@Data
public class ExpenseRequest {

    @NotBlank(message = "title is required")
    private String title;

    @Positive(message = "amount must be greater than 0")
    private double amount;

    @NotBlank(message = "category is required")
    private String category;

    @NotNull(message = "date is required")
    private LocalDate date;
}
