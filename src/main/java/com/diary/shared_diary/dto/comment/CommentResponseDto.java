package com.diary.shared_diary.dto.comment;

import com.diary.shared_diary.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
    private Long parentId;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorUsername = comment.getAuthor().getUsername();
        this.createdAt = comment.getCreatedAt();
        this.parentId = comment.getParent() != null ? comment.getParent().getId() : null;
    }
}
