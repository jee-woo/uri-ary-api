package com.diary.shared_diary.dto.diary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class DiaryRequestDto {
    private String title;
    private String encryptedContent;
    private String iv;
    private String authTag;
    private MultipartFile image;
    private List<EncryptedDiaryKeyDto> keys;
}