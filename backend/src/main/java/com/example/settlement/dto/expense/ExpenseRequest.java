package com.example.settlement.dto.expense;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ExpenseRequest(
        @NotBlank(message = "支出名は必須です")
        @Size(max = 100, message = "支出名は100文字以内で入力してください")
        String title,

        @Min(value = 1, message = "金額は1円以上で入力してください")
        int amount,

        @NotNull(message = "支払者は必須です")
        Long paidByMemberId,

        @NotNull(message = "支払日は必須です")
        LocalDate paidAt,

        String memo,

        @NotEmpty(message = "対象者は1人以上必要です")
        List<Long> targetMemberIds
) {
}
