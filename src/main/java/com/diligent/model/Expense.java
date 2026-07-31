package com.diligent.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    private Long id;
    private String title;
    private double amount;
    private String category;
    private LocalDate date;
}
