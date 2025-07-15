package com.diary.shared_diary.dto.diary;

import com.diary.shared_diary.domain.Diary;
import lombok.Getter;

import java.time.LocalDateTime;

import com.diary.shared_diary.dto.comment.CommentResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DiaryDetailResponseDto {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
    private List<CommentResponseDto> comments;

    public DiaryDetailResponseDto(Diary diary) {
        this.id = diary.getId();
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.authorUsername = diary.getAuthor().getUsername();
        this.createdAt = diary.getCreatedAt();
        this.comments = diary.getComments().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }
}
