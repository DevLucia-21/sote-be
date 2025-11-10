// src/main/java/com/fluxion/sote/setting/controller/SettingController.java
package com.fluxion.sote.setting.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.dto.FcmTokenRequest;
import com.fluxion.sote.setting.dto.NotificationSettingRequest;
import com.fluxion.sote.setting.dto.NotificationSettingResponse;
import com.fluxion.sote.setting.dto.ThemeSettingRequest;
import com.fluxion.sote.setting.dto.ThemeSettingResponse;
import com.fluxion.sote.setting.enums.DeviceType;
import com.fluxion.sote.setting.service.FCMService;
import com.fluxion.sote.setting.service.FcmTokenService;
import com.fluxion.sote.setting.service.SettingService;
import com.fluxion.sote.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;
    private final FcmTokenService fcmTokenService;
    private final FCMService fcmService;
    private final UserRepository userRepository;

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
        return ResponseEntity.ok(new ThemeSettingResponse(settingService.getCurrentThemeSetting()));
    }

    /** 테마 설정 수정 */
    @PutMapping("/theme")
    public ResponseEntity<Void> updateThemeSetting(
            @Valid @RequestBody ThemeSettingRequest request) {
        settingService.updateThemeSetting(request.isDarkMode());
        return ResponseEntity.noContent().build();
    }

    //FCM 토큰 등록 (앱 or 워치)
    @PostMapping("/token")
    public ResponseEntity<Void> registerFcmToken(
            @Valid @RequestBody FcmTokenRequest request) {
        DeviceType deviceType = request.getDeviceType() != null
                ? request.getDeviceType()
                : DeviceType.MOBILE;

        fcmTokenService.saveToken(request.getToken(), deviceType);
        return ResponseEntity.status(201).build();
    }

   //FCM 토큰 삭제
    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteFcmToken(@RequestParam("token") String token) {
        fcmTokenService.deleteToken(token);
        return ResponseEntity.noContent().build();
    }

    //특정 기기로 테스트 발송 (기존)
    @PostMapping("/send")
    public ResponseEntity<Void> sendTestNotification(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam(defaultValue = "MOBILE") DeviceType deviceType) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        fcmService.sendNotificationToUser(user, deviceType, title, body);
        return ResponseEntity.ok().build();
    }

   //모든 기기(MOBILE + WATCH)에 동시 발송
   @PostMapping("/send/all")
   public ResponseEntity<Map<String, Object>> sendNotificationToAllDevices(
           @RequestParam Long userId,
           @RequestParam String title,
           @RequestParam String body) {

       User user = userRepository.findById(userId)
               .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

       Map<String, Object> result = fcmService.sendNotificationToAllDevices(user, title, body);
       return ResponseEntity.ok(result);
   }
}
