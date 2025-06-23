package com.diary.shared_diary.dto.group;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupMemberAddRequestDto {
    private List<Long> userIds;
}
