package com.diary.shared_diary.dto.comment;

public record CommentRequestDto(
        String content,
        Long parentId
) {}
