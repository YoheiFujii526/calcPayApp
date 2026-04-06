package com.example.settlement.dto.settlement;

public record SettlementTransfer(
        Long fromMemberId,
        Long toMemberId,
        int amount
) {
}
