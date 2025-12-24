package com.diary.shared_diary.controller;

import com.diary.shared_diary.auth.CustomUserDetails;
import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.user.PublicKeyRequestDto;
import com.diary.shared_diary.dto.user.UserRequestDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto dto) {
        log.info("[USER-CREATE] Request to create user: {}", dto.email());
        return userService.createUser(dto);
    }


    @GetMapping
    public List<UserResponseDto> getUsers() {
        log.info("[USER-GET-ALL] Request to get all users.");
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        }

        UserResponseDto userResponse = userService.getUserResponseByEmail(userDetails.getUsername());

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/exists")
    public boolean checkEmail(@RequestParam String email) {
        log.info("[USER-CHECK-EMAIL] Request to check email: {}", email);
        return userService.isEmailExists(email);
    }

    @PostMapping("/public-key")
    public ResponseEntity<Void> updatePublicKey(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PublicKeyRequestDto request
    ) {
        if (userDetails == null) {
            throw new AccessDeniedException("인증된 사용자가 아닙니다.");
        }

        String email = userDetails.getUsername();
        log.info("[USER-UPDATE-KEY] Updating public key for user: {}", email);

        userService.updatePublicKey(email, request.publicKey());

        return ResponseEntity.ok().build();
    }
}