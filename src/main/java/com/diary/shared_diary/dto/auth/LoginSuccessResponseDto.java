package com.diary.shared_diary.dto.auth;

public record LoginSuccessResponseDto(
        String email,
        String username,
        String token
) {}
