package com.diary.shared_diary.dto.dev;


public record DevLoginResponseDto (
        String email,
        String username,
        String token
) {}

