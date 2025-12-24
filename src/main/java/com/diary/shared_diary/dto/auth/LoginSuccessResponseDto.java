package com.diary.shared_diary.dto.auth;

import com.diary.shared_diary.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessResponseDto {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}