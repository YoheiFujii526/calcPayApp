package com.example.settlement.dto.repayment;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RepaymentResponse(
        Long id,
        Long eventId,
        Long fromMemberId,
        String fromMemberName,
        Long toMemberId,
        String toMemberName,
        int amount,
        LocalDate repaidAt,
        String memo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
