package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.JwtUtil;
import com.diary.shared_diary.config.OAuth2Properties;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OAuth2Properties oAuth2Properties;

    @GetMapping("/login/success")
    public void loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User,
                             HttpServletResponse response) throws IOException {
        if (oauth2User == null) {
            response.sendError(401, "인증 실패");
            return;
        }

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
        String redirectUri = oAuth2Properties.getRedirectUri();

        response.sendRedirect(redirectUri + "?token=" + token);
    }
}
