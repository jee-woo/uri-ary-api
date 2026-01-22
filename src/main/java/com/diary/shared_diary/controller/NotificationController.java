package com.diary.shared_diary.controller;

import com.diary.shared_diary.dto.notification.NotificationResponseDto;
import com.diary.shared_diary.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "사용자 알림 목록 조회")
    @GetMapping
    public List<NotificationResponseDto> getNotifications(@AuthenticationPrincipal Long userId) {
        return notificationService.getNotifications(userId);
    }

    @Operation(summary = "알림 읽음 처리")
    @PostMapping("/{notificationId}/read")
    public void readNotification(@PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
    }
}
