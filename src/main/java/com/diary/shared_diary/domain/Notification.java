package com.diary.shared_diary.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver; // 알림을 받는 사람

    private String message; // "OOO님이 '우리집' 그룹 참여를 요청했습니다."

    @Enumerated(EnumType.STRING)
    private NotificationType type; // REQUEST, COMMENT, SYSTEM 등

    private Long targetId; // 이동할 타겟 ID (예: groupId)

    private boolean isRead; // 읽음 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Notification(User receiver, String message, NotificationType type, Long targetId) {
        this.receiver = receiver;
        this.message = message;
        this.type = type;
        this.targetId = targetId;
        this.isRead = false;
    }

    public void read() {
        this.isRead = true;
    }
}
