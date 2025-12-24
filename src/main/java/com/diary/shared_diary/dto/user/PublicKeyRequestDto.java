package com.diary.shared_diary.dto.user;

import jakarta.validation.constraints.NotBlank;

public record PublicKeyRequestDto(
        @NotBlank(message = "공개키는 필수 항목입니다.")
        String publicKey
) {}