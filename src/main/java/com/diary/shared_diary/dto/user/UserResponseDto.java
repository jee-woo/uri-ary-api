package com.diary.shared_diary.dto.user;

import com.diary.shared_diary.domain.User;

public record UserResponseDto(
        Long id,
        String username,
        String email
) {
    public UserResponseDto(User user) {
        this(user.getId(), user.getUsername(), user.getEmail());
    }
}
