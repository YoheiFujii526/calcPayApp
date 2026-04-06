package com.example.settlement.dto.settlement;

public record SettlementMemberBalance(
        Long memberId,
        String memberName,
        int totalPaid,
        int fairShare,
        int receivedRepayment,
        int sentRepayment,
        int balance
) {
}
