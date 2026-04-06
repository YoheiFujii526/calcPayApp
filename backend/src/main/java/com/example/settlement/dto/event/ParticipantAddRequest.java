package com.example.settlement.dto.event;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ParticipantAddRequest(
        @NotEmpty(message = "メンバーIDは1件以上必要です")
        List<Long> memberIds
) {
}
