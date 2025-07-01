package com.diary.shared_diary.dto.diary;

import com.diary.shared_diary.domain.Diary;
import com.diary.shared_diary.util.S3Uploader;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryResponseDto {

    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
    private String imageUrl;

    public DiaryResponseDto(Diary diary, S3Uploader uploader) {
        this.id = diary.getId();
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.authorUsername = diary.getAuthor().getUsername();
        this.createdAt = diary.getCreatedAt();
        this.imageUrl = diary.getImagePath() != null
                ? uploader.getPresignedUrl(diary.getImagePath())
                : null;
    }
}
