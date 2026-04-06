package com.example.settlement.dto.member;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String name,
        String displayName,
        String grade,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
