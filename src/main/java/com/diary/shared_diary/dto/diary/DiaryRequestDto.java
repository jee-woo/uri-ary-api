package com.diary.shared_diary.dto.diary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DiaryRequestDto {
    private String title;
    private String encryptedContent;
    private MultipartFile image;

    private String iv;
    private String authTag;
    private String encryptedAesKey;
}