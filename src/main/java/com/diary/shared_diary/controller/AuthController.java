package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.JwtUtil;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.auth.LoginSuccessResponseDto;
import com.diary.shared_diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @GetMapping("/login/success")
    public ResponseEntity<LoginSuccessResponseDto> loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(email)
                        .username(nickname)
                        .createdAt(LocalDateTime.now())
                        .build()));

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginSuccessResponseDto(email, nickname, token));
    }
}
