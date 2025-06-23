package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.User;
import com.diary.shared_diary.dto.user.UserRequestDto;
import com.diary.shared_diary.dto.user.UserResponseDto;
import com.diary.shared_diary.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDto createUser(UserRequestDto dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return new UserResponseDto(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponseDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                ))
                .toList();
    }


    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
