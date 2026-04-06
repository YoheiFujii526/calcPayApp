package com.example.settlement.controller;

import com.example.settlement.dto.settlement.SettlementResponse;
import com.example.settlement.service.SettlementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/api/events/{id}/settlement")
    public SettlementResponse getSettlement(@PathVariable("id") Long eventId) {
        return settlementService.calculate(eventId);
    }
}
