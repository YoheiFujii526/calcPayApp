package com.example.settlement.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberRequest(
        @NotBlank(message = "名前は必須です")
        @Size(max = 50, message = "名前は50文字以内で入力してください")
        String name,

        @Size(max = 50, message = "表示名は50文字以内で入力してください")
        String displayName,

        @Size(max = 20, message = "学年・属性は20文字以内で入力してください")
        String grade,

        Boolean isActive
) {
}
