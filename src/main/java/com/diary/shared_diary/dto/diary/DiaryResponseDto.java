package com.diary.shared_diary.dto.diary;

import com.diary.shared_diary.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DiaryResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private UserResponseDto author;
}
