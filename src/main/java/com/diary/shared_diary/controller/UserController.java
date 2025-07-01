package com.diary.shared_diary.controller;

import com.diary.shared_diary.dto.user.UserRequestDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponseDto createUser(@RequestBody UserRequestDto dto) {
        return userService.createUser(dto);
    }


    @GetMapping
    public List<UserResponseDto> getUsers() {
        return userService.getAllUsers();
    }


    @GetMapping("/exists")
    public boolean checkEmail(@RequestParam String email) {
        return userService.isEmailExists(email);
    }
}
