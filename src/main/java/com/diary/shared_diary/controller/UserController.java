package com.diary.shared_diary.controller;

import com.diary.shared_diary.dto.user.UserRequestDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.service.UserService;
import lombok.extern.slf4j.Slf4j;
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


    @GetMapping("/exists")
    public boolean checkEmail(@RequestParam String email) {
        log.info("[USER-CHECK-EMAIL] Request to check email: {}", email);
        return userService.isEmailExists(email);
    }
}
