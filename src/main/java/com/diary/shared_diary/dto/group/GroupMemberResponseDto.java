package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupMemberResponseDto {
    private Long userId;
    private String username;
    private String publicKey;

    public static GroupMemberResponseDto from(GroupMember groupMember) {
        return new GroupMemberResponseDto(
                groupMember.getUser().getId(),
                groupMember.getUser().getUsername(),
                groupMember.getUser().getPublicKey()
        );
    }
}
