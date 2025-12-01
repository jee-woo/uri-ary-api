package com.diary.shared_diary.dto.diary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DiaryRequestDto {
    private String title;
    private String content;
    private MultipartFile image;
}