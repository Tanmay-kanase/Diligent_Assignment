package com.diligent.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.diligent.exception.ExpenseNotFoundException;
import com.diligent.exception.GlobalExceptionHandler;
import com.diligent.model.Expense;
import com.diligent.service.CalculateService;
import com.diligent.service.ExpenseService;
import com.diligent.service.FilterExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private FilterExpenseService filterExpenseService;

    @Mock
    private CalculateService calculateService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        ExpenseController controller = new ExpenseController(expenseService, filterExpenseService, calculateService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private Expense sampleExpense() {
        return new Expense(1L, "Coffee", 5.0, "Food", LocalDate.of(2026, 7, 1));
    }

    @Test
    void addExpense_returns201_withCreatedExpense() throws Exception {
        Expense created = sampleExpense();
        when(expenseService.addExpense(any())).thenReturn(created);

        String body = """
                {
                  "title": "Coffee",
                  "amount": 5.0,
                  "category": "Food",
                  "date": "2026-07-01"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Coffee"))
                .andExpect(jsonPath("$.amount").value(5.0));
    }

    @Test
    void addExpense_returns400_whenValidationFails() throws Exception {
        String invalidBody = """
                {
                  "title": "",
                  "amount": -5.0,
                  "category": "Food",
                  "date": "2026-07-01"
                }
                """;

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllExpenses_returnsList() throws Exception {
        when(expenseService.getAllExpenses()).thenReturn(List.of(sampleExpense()));

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Coffee"));
    }

    @Test
    void getExpenseById_returnsExpense_whenFound() throws Exception {
        when(expenseService.getExpenseById(1L)).thenReturn(sampleExpense());

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getExpenseById_returns404_whenNotFound() throws Exception {
        when(expenseService.getExpenseById(99L)).thenThrow(new ExpenseNotFoundException(99L));

        mockMvc.perform(get("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense not found with id: 99"));
    }

    @Test
    void searchExpenses_passesKeywordAndCategoryToService() throws Exception {
        when(filterExpenseService.searchExpenses("coffee", "Food")).thenReturn(List.of(sampleExpense()));

        mockMvc.perform(get("/api/expenses/search")
                .param("category", "Food")
                .param("keyword", "coffee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(filterExpenseService).searchExpenses("coffee", "Food");
    }

    @Test
    void searchExpenses_worksWithNoParams() throws Exception {
        when(filterExpenseService.searchExpenses(isNull(), isNull())).thenReturn(List.of(sampleExpense()));

        mockMvc.perform(get("/api/expenses/search"))
                .andExpect(status().isOk());
    }

    @Test
    void filterByCategory_returnsMatchingExpenses() throws Exception {
        when(filterExpenseService.filterByCategory("Food")).thenReturn(List.of(sampleExpense()));

        mockMvc.perform(get("/api/expenses/category/Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Food"));
    }

    @Test
    void getTotalExpenses_returnsOverallTotal() throws Exception {
        when(calculateService.calculateTotal()).thenReturn(52.5);

        mockMvc.perform(get("/api/expenses/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(52.5));
    }

    @Test
    void getTotalByCategory_returnsCategoryMap() throws Exception {
        when(calculateService.calculateTotalByCategory()).thenReturn(Map.of("Food", 50.0, "Travel", 2.5));

        mockMvc.perform(get("/api/expenses/total/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Food").value(50.0))
                .andExpect(jsonPath("$.Travel").value(2.5));
    }

    @Test
    void getMonthlySummary_returnsMonthlyMap() throws Exception {
        when(calculateService.monthlySummary()).thenReturn(Map.of("2026-07", 55.0));

        mockMvc.perform(get("/api/expenses/summary/monthly"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['2026-07']").value(55.0));
    }

    @Test
    void deleteExpense_returns204_whenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isNoContent());

        verify(expenseService).deleteExpense(1L);
    }

    @Test
    void deleteExpense_returns404_whenExpenseNotFound() throws Exception {
        doThrow(new ExpenseNotFoundException(99L)).when(expenseService).deleteExpense(99L);

        mockMvc.perform(delete("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense not found with id: 99"));
    }
}
