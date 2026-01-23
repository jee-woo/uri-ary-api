package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.MemberStatus;

public record GroupResponseDto(
        Long id,
        String name,
        String code,
        MemberStatus status
) {}
