package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.MemberStatus;

import java.time.LocalDateTime;

public record GroupResponseDto(
        Long id,
        String name,
        String code,
        MemberStatus status,
        int memberCount,
        LocalDateTime lastDiaryAt
) {}
