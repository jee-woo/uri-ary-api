package com.diary.shared_diary.dto.group;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ApproveMemberRequest {
    private List<ReEncryptedKeyDto> reEncryptedKeys;
}
