package com.diary.shared_diary.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginSuccessResponseDto {
    private String accessToken;
    private String refreshToken;
}