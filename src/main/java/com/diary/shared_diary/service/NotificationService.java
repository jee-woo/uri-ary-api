package com.diary.shared_diary.service;

import com.diary.shared_diary.domain.Notification;
import com.diary.shared_diary.dto.notification.NotificationResponseDto;
import com.diary.shared_diary.exception.NotFoundException;
import com.diary.shared_diary.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<NotificationResponseDto> getNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverId(userId);
        log.info("Fetching notifications for user ID: {}", userId);
        return notifications.stream()
                .map(NotificationResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 알림입니다."));
        notification.read();
    }
}
