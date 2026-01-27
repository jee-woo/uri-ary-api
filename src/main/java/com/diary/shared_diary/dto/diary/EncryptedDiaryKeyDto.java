package com.diary.shared_diary.dto.diary;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EncryptedDiaryKeyDto {
    private Long userId;
    private String encryptedAesKey;
}
