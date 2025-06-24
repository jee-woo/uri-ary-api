package com.diary.shared_diary.dto.diary;

import com.diary.shared_diary.domain.Diary;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DiaryResponseDto {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;

    public DiaryResponseDto(Diary diary) {
        this.id = diary.getId();
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.authorName = diary.getAuthor().getUsername();
        this.createdAt = diary.getCreatedAt();
    }
}
