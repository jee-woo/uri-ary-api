package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.DiaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiaryKeyDto {
    private Long diaryId;
    private String encryptedAesKey;

    public static DiaryKeyDto from(DiaryKey diaryKey) {
        return new DiaryKeyDto(diaryKey.getDiary().getId(), diaryKey.getEncryptedAesKey());
    }
}
