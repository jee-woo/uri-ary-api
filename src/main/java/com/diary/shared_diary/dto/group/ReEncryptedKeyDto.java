package com.diary.shared_diary.dto.group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReEncryptedKeyDto {
    private Long diaryId;
    private String encryptedAesKey;
}