package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.JwtUtil;
import com.diary.shared_diary.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, String>> devLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (!userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "사용자 없음"));
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));
    }
}

