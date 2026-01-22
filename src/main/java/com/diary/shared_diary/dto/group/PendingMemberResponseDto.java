package com.diary.shared_diary.dto.group;

import com.diary.shared_diary.domain.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 외부에서 생성자 직접 호출 방지
@Builder
public class PendingMemberResponseDto {
    private Long userId;
    private String username;
    private String email;
    private String publicKey;

    public static PendingMemberResponseDto from(User user) {
        return PendingMemberResponseDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .publicKey(user.getPublicKey())
                .build();
    }
}