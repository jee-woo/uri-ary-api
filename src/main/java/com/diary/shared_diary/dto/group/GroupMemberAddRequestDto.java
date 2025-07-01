package com.diary.shared_diary.dto.group;


import java.util.List;

public record GroupMemberAddRequestDto(List<Long> userIds) {
}
