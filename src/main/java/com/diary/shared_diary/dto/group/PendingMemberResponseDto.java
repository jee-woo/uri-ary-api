package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.GroupMember;
import lombok.Getter;

@Getter
public class PendingMemberResponseDto {
    private Long userId;
    private String username;
    private String email;
    private String publicKey;

    public PendingMemberResponseDto(GroupMember groupMember) {
        this.userId = groupMember.getUser().getId();
        this.username = groupMember.getUser().getUsername();
        this.email = groupMember.getUser().getEmail();
        this.publicKey = groupMember.getUser().getPublicKey();
    }
}