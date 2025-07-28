package com.fluxion.sote.notification.controller;

import com.fluxion.sote.notification.dto.NotificationSettingRequest;
import com.fluxion.sote.notification.dto.NotificationSettingResponse;
import com.fluxion.sote.notification.service.NotificationService;
import com.fluxion.sote.notification.service.FCMService;
import com.fluxion.sote.global.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final FCMService fcmService;

    /**
     * 알림 설정 조회
     */
    @GetMapping("/settings")
    public NotificationSettingResponse getMyNotificationSettings() {
        return notificationService.getMySettings();
    }

    /**
     * 알림 설정 수정
     */
    @PutMapping("/settings")
    public void updateMyNotificationSettings(@Valid @RequestBody NotificationSettingRequest request) {
        notificationService.updateMySettings(request);
    }

    /**
     * 푸시 알림 전송 (테스트용)
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendTestNotification(@RequestParam String targetToken,
                                                       @RequestParam String title,
                                                       @RequestParam String body) {
        fcmService.sendNotification(targetToken, title, body);
        return ResponseEntity.ok("Notification sent successfully.");
    }
}
