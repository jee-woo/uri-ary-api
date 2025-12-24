package com.diary.shared_diary.dto.diary;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.util.S3Uploader;
import lombok.Getter;

import java.time.LocalDateTime;

import com.diary.shared_diary.dto.comment.CommentResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DiaryDetailResponseDto {
    private Long id;
    private String title;
    private String encryptedContent;
    private String authorUsername;
    private LocalDateTime createdAt;
    private String imageUrl;
    private List<CommentResponseDto> comments;
    private String iv;
    private String authTag;
    private String encryptedAesKey;

    public DiaryDetailResponseDto(Diary diary, S3Uploader uploader) {
        this.id = diary.getId();
        this.title = diary.getTitle();
        this.encryptedContent = diary.getEncryptedContent();
        this.authorUsername = diary.getAuthor().getUsername();
        this.createdAt = diary.getCreatedAt();
        this.imageUrl = diary.getImagePath() != null
                ? uploader.getPresignedUrl(diary.getImagePath())
                : null;
        this.comments = diary.getComments().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());

        this.iv = diary.getIv();
        this.authTag = diary.getAuthTag();
        this.encryptedAesKey = diary.getEncryptedAesKey();
    }
}
