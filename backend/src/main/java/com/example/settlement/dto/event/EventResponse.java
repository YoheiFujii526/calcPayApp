package com.example.settlement.dto.event;

import com.example.settlement.entity.EventStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        LocalDate eventDate,
        String description,
        EventStatus status,
        int participantCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
