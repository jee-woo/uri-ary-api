package com.diary.shared_diary.dto.group;

import jakarta.validation.constraints.NotBlank;

public record JoinGroupRequestDto(
        @NotBlank(message = "그룹 코드는 필수입니다.")
        String code
) {}
