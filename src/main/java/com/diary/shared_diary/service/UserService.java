package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.user.UserRequestDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        log.info("Creating user with email: {}", dto.email());
        User user = User.builder()
                .username(dto.username())
                .email(dto.email())
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return new UserResponseDto(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    public List<UserResponseDto> getAllUsers() {
        log.info("Fetching all users.");
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
                .toList();
        log.info("Found {} users.", users.size());
        return users;
    }


    public boolean isEmailExists(String email) {
        log.info("Checking if email exists: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.info("Email {} exists: {}", email, exists);
        return exists;
    }
}
