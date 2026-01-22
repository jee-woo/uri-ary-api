package com.diary.shared_diary.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    private MemberStatus status; // PENDING 또는 ACCEPTED

    private LocalDateTime joinedAt;

    public void approve() {
        if (this.status != MemberStatus.PENDING) {
            throw new IllegalStateException("대기 중인 요청만 승인할 수 있습니다.");
        }
        this.status = MemberStatus.ACCEPTED;
        this.joinedAt = LocalDateTime.now(); // 승인된 시점을 기록
    }

    public static GroupMember createPendingMember(User user, Group group) {
        return GroupMember.builder()
                .user(user)
                .group(group)
                .status(MemberStatus.PENDING)
                .joinedAt(LocalDateTime.now())
                .build();
    }
}