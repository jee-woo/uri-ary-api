package com.diary.shared_diary.controller;

import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/dev")
@Profile("dev")
public class DevUserController {

    private final UserRepository userRepository;

    public DevUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create-test-user")
    public ResponseEntity<String> createTestUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("테스트 계정 생성 완료");
    }
}
