package com.example.settlement.controller;

import com.example.settlement.dto.expense.ExpenseRequest;
import com.example.settlement.dto.expense.ExpenseResponse;
import com.example.settlement.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/api/events/{id}/expenses")
    public List<ExpenseResponse> getExpenses(@PathVariable("id") Long eventId) {
        return expenseService.findByEventId(eventId);
    }

    @PostMapping("/api/events/{id}/expenses")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(@PathVariable("id") Long eventId, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.create(eventId, request);
    }

    @PutMapping("/api/expenses/{id}")
    public ExpenseResponse updateExpense(@PathVariable("id") Long expenseId, @Valid @RequestBody ExpenseRequest request) {
        return expenseService.update(expenseId, request);
    }

    @DeleteMapping("/api/expenses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable("id") Long expenseId) {
        expenseService.delete(expenseId);
    }
}
