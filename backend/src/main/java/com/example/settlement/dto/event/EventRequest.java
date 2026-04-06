package com.example.settlement.dto.event;

import com.example.settlement.entity.EventStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record EventRequest(
        @NotBlank(message = "イベント名は必須です")
        @Size(max = 100, message = "イベント名は100文字以内で入力してください")
        String title,

        @NotNull(message = "開催日は必須です")
        LocalDate eventDate,

        String description,

        EventStatus status
) {
}
