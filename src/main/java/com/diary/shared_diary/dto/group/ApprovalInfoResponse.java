package com.diary.shared_diary.dto.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ApprovalInfoResponse {
    private String newMemberPublicKey;
    private List<DiaryKeyDto> diaryKeys;
}
