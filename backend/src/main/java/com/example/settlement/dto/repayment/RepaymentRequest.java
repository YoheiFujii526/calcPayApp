package com.example.settlement.dto.repayment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RepaymentRequest(
        @NotNull(message = "返済者は必須です")
        Long fromMemberId,

        @NotNull(message = "受取者は必須です")
        Long toMemberId,

        @Min(value = 1, message = "金額は1円以上で入力してください")
        int amount,

        @NotNull(message = "返済日は必須です")
        LocalDate repaidAt,

        String memo
) {
}
