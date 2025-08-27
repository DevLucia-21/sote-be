// src/main/java/com/fluxion/sote/setting/controller/SettingController.java
package com.fluxion.sote.setting.controller;

import com.fluxion.sote.setting.dto.FcmTokenRequest;
import com.fluxion.sote.setting.dto.NotificationSettingRequest;
import com.fluxion.sote.setting.dto.NotificationSettingResponse;
import com.fluxion.sote.setting.dto.ThemeSettingRequest;
import com.fluxion.sote.setting.dto.ThemeSettingResponse;
import com.fluxion.sote.setting.service.FCMService;
import com.fluxion.sote.setting.service.FcmTokenService;
import com.fluxion.sote.setting.service.SettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;
    private final FcmTokenService fcmTokenService;
    private final FCMService fcmService;    // <-- FCMService 주입

    /** 알림 설정 조회 */
    @GetMapping("/notifications")
    public ResponseEntity<NotificationSettingResponse> getNotificationSettings() {
        return ResponseEntity.ok(settingService.getMySettings());
    }

    /** 알림 설정 수정 */
    @PutMapping("/notifications")
    public ResponseEntity<Void> updateNotificationSettings(
            @Valid @RequestBody NotificationSettingRequest request) {
        settingService.updateMySettings(request);
        return ResponseEntity.noContent().build();
    }

    /** 테마 설정 조회 */
    @GetMapping("/theme")
    public ResponseEntity<ThemeSettingResponse> getThemeSetting() {
        return ResponseEntity.ok(new ThemeSettingResponse(
                settingService.getCurrentThemeSetting()
        ));
    }

    /** 테마 설정 수정 */
    @PutMapping("/theme")
    public ResponseEntity<Void> updateThemeSetting(
            @Valid @RequestBody ThemeSettingRequest request) {
        settingService.updateThemeSetting(request.isDarkMode());
        return ResponseEntity.noContent().build();
    }

    /** FCM 토큰 등록 (앱 시작 시) */
    @PostMapping("/token")
    public ResponseEntity<Void> registerFcmToken(
            @Valid @RequestBody FcmTokenRequest request) {
        fcmTokenService.saveToken(request.getToken());
        return ResponseEntity.status(201).build();
    }

    /** FCM 토큰 삭제 (로그아웃 시) */
    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteFcmToken(
            @RequestParam("token") String token) {
        fcmTokenService.deleteToken(token);
        return ResponseEntity.noContent().build();
    }

    /** 푸시 알림 전송 (테스트용) */
    @PostMapping("/send")
    public ResponseEntity<Void> sendTestNotification(
            @RequestParam String targetToken,
            @RequestParam String title,
            @RequestParam String body) {
        fcmService.sendNotification(targetToken, title, body);
        return ResponseEntity.ok().build();
    }
}
