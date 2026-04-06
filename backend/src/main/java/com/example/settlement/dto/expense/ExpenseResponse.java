package com.example.settlement.dto.expense;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ExpenseResponse(
        Long id,
        Long eventId,
        String title,
        int amount,
        Long paidByMemberId,
        String paidByMemberName,
        LocalDate paidAt,
        String memo,
        List<Long> targetMemberIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
