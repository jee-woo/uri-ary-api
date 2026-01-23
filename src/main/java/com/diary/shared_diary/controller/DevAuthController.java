package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.JwtUtil;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.auth.LoginSuccessResponseDto;
import com.diary.shared_diary.dto.dev.DevLoginRequestDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile("dev")
public class DevAuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public DevAuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginSuccessResponseDto> devLogin(@RequestBody DevLoginRequestDto request) {
        String email = request.email();

        return userRepository.findByEmail(email)
                .map(user -> {
                    String accessToken = jwtUtil.generateAccessToken(user.getId(), email);
                    String refreshToken = jwtUtil.generateRefreshToken(user.getId(), email);
                    user.setRefreshToken(refreshToken);
                    userRepository.save(user);
                    return ResponseEntity.ok(new LoginSuccessResponseDto(
                            accessToken,
                            refreshToken,
                            new UserResponseDto(user.getId(), user.getUsername(), user.getEmail(), user.getPublicKey())
                    ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }


}

