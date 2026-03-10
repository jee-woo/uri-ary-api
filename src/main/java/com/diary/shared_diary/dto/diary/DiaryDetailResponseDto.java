package com.diary.shared_diary.dto.diary;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.domain.DiaryKey;
import com.diary.shared_diary.dto.comment.CommentResponseDto;
import com.diary.shared_diary.util.S3Uploader;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DiaryDetailResponseDto {
    private Long id;
    private String title;
    private String encryptedContent;
    private String iv; // for content
    private String authTag; // for content
    private String authorUsername;
    private String imageUrl;
    private List<CommentResponseDto> comments;
    private LocalDateTime createdAt;
    private boolean isMine;
    private EncryptedKeyInfo keyInfo; // for key

    @Getter
    @AllArgsConstructor
    public static class EncryptedKeyInfo {
        private String encryptedAesKey;
    }

    public DiaryDetailResponseDto(Diary diary, DiaryKey diaryKey, S3Uploader uploader, boolean isMine) {
        this.id = diary.getId();
        this.title = diary.getTitle();
        this.encryptedContent = diary.getEncryptedContent();
        this.iv = diary.getIv(); // from Diary
        this.authTag = diary.getAuthTag(); // from Diary
        this.authorUsername = diary.getAuthor().getUsername();
        this.createdAt = diary.getCreatedAt();
        this.imageUrl = diary.getImagePath() != null
                ? uploader.getPresignedUrl(diary.getImagePath())
                : null;
        this.isMine = isMine;
        this.comments = diary.getComments().stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());

        this.keyInfo = new EncryptedKeyInfo(
                diaryKey.getEncryptedAesKey()
        );
    }
}
