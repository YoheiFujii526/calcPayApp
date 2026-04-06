package com.example.settlement.controller;

import com.example.settlement.dto.repayment.RepaymentRequest;
import com.example.settlement.dto.repayment.RepaymentResponse;
import com.example.settlement.service.RepaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RepaymentController {

    private final RepaymentService repaymentService;

    public RepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    @GetMapping("/api/events/{id}/repayments")
    public List<RepaymentResponse> getRepayments(@PathVariable("id") Long eventId) {
        return repaymentService.findByEventId(eventId);
    }

    @PostMapping("/api/events/{id}/repayments")
    @ResponseStatus(HttpStatus.CREATED)
    public RepaymentResponse createRepayment(@PathVariable("id") Long eventId, @Valid @RequestBody RepaymentRequest request) {
        return repaymentService.create(eventId, request);
    }

    @PutMapping("/api/repayments/{id}")
    public RepaymentResponse updateRepayment(@PathVariable("id") Long repaymentId, @Valid @RequestBody RepaymentRequest request) {
        return repaymentService.update(repaymentId, request);
    }

    @DeleteMapping("/api/repayments/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRepayment(@PathVariable("id") Long repaymentId) {
        repaymentService.delete(repaymentId);
    }
}
