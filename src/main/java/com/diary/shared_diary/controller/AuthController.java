package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.JwtUtil;
import com.diary.shared_diary.config.OAuth2Properties;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.auth.LoginSuccessResponseDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.exception.InvalidTokenException;
import com.diary.shared_diary.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OAuth2Properties oAuth2Properties;

    @GetMapping("/login/success")
    public void loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User, HttpServletResponse response) throws IOException {
        if (oauth2User == null) {
            log.warn("[AUTH-LOGIN-FAIL] Authentication failed, OAuth2User is null.");
            response.sendError(401, "인증 실패");
            return;
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map<String, Object>) kakaoAccount.get("profile")).get("nickname");
        log.info("[AUTH-LOGIN-SUCCESS] User logged in successfully: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("New user detected, creating a new user account for: {}", email);
                    return userRepository.save(User.builder()
                        .email(email)
                        .username(nickname)
                        .createdAt(LocalDateTime.now())
                        .build());
                });

        String authorizationCode = UUID.randomUUID().toString();
        user.setAuthorizationCode(authorizationCode);
        user.setAuthorizationCodeExpiresAt(LocalDateTime.now().plusMinutes(1)); // 1분 후 만료
        userRepository.save(user);
        log.info("Generated authorization code for user: {}", email);

        String redirectUri = oAuth2Properties.getRedirectUri();
        response.sendRedirect(redirectUri + "?code=" + authorizationCode);
    }

    @PostMapping("/api/auth/token")
    public ResponseEntity<LoginSuccessResponseDto> exchangeCodeForTokens(@RequestBody Map<String, String> request) {
        String authorizationCode = request.get("code");
        log.info("[AUTH-TOKEN-EXCHANGE] Attempting to exchange authorization code for tokens.");
        User user = userRepository.findByAuthorizationCode(authorizationCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid authorization code"));

        if (user.getAuthorizationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("[AUTH-TOKEN-EXCHANGE] Authorization code has expired for user: {}", user.getEmail());
            throw new IllegalArgumentException("Authorization code has expired");
        }

        user.setAuthorizationCode(null);
        user.setAuthorizationCodeExpiresAt(null);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        log.info("[AUTH-TOKEN-EXCHANGE] Successfully exchanged code for tokens for user: {}", user.getEmail());
        UserResponseDto userResponse = UserResponseDto.from(user);

        return ResponseEntity.ok(new LoginSuccessResponseDto(accessToken, refreshToken, userResponse));
    }

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String oldRefreshToken = request.get("refreshToken");
        log.info("[AUTH-TOKEN-REFRESH] Attempting to refresh token.");

        try {
            // 1. JWT 유효성 검사 및 사용자 이메일 추출
            String email = jwtUtil.validateAndGetEmail(oldRefreshToken);

            // 2. DB에서 리프레시 토큰 일치 여부 확인 (토큰 소유자 확인)
            User user = userRepository.findByRefreshToken(oldRefreshToken)
                    .orElseThrow(() -> new InvalidTokenException("Invalid or Expired refresh token"));

            if (!email.equals(user.getEmail())) {
                // 토큰 내부의 이메일과 DB에 저장된 사용자의 이메일이 다르면 비정상 접근
                log.warn("[AUTH-TOKEN-REFRESH] Mismatched refresh token owner. Token email: {}, User email: {}", email, user.getEmail());
                throw new InvalidTokenException("Mismatched refresh token owner");
            }

            // --- 리프레시 토큰 로테이션 (RTR) 적용 시작 ---

            // 3. 새로운 액세스 토큰과 리프레시 토큰 발급
            String newAccessToken = jwtUtil.generateAccessToken(email);
            String newRefreshToken = jwtUtil.generateRefreshToken(email);

            // 4. DB에 새로운 리프레시 토큰 저장 (기존 토큰 폐기 효과)
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);
            log.info("[AUTH-TOKEN-REFRESH] Successfully refreshed token for user: {}", email);

            UserResponseDto userResponse = UserResponseDto.from(user);

            // 5. 새로운 액세스 토큰과 새로운 리프레시 토큰을 클라이언트에 전달
            return ResponseEntity.ok(new LoginSuccessResponseDto(newAccessToken, newRefreshToken, userResponse));

            // --- RTR 적용 완료 ---
        } catch (ExpiredJwtException e) {
            log.warn("[AUTH-TOKEN-REFRESH] Refresh token has expired.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token has expired.");
        }
    }
}
