package com.fluxion.sote.setting.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.entity.FcmToken;
import com.fluxion.sote.setting.enums.DeviceType;
import com.fluxion.sote.setting.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final FcmTokenRepository fcmTokenRepository;

    //내부 전송 로직 (Firebase 예외 감지 포함)
    private String sendNotificationInternal(String targetToken, String title, String body)
            throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .build();

        try {
            // Firebase로 메시지 발송
            return FirebaseMessaging.getInstance().send(message);

        } catch (FirebaseMessagingException e) {
            // 만료된 토큰 처리
            if (isTokenExpired(e)) {
                log.warn("[FCM] 만료된 토큰 감지됨 → DB에서 삭제: {}", targetToken);
                fcmTokenRepository.deleteExpiredToken(targetToken);
            }
            throw e;
        }
    }

    //예외가 만료 토큰 관련인지 판별
    private boolean isTokenExpired(FirebaseMessagingException e) {
        MessagingErrorCode code = e.getMessagingErrorCode();
        return code == MessagingErrorCode.UNREGISTERED
                || code == MessagingErrorCode.INVALID_ARGUMENT;
    }

    // 특정 디바이스 타입에 전송 (기존 /send)
    @Transactional
    public void sendNotificationToUser(User user, DeviceType deviceType, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findAllByUserAndDeviceType(user, deviceType);
        if (tokens.isEmpty()) {
            log.warn("⚠️ [FCM] userId={} / deviceType={} 등록된 토큰 없음", user.getId(), deviceType);
            return;
        }

        for (FcmToken token : tokens) {
            try {
                String response = sendNotificationInternal(token.getToken(), title, body);
                log.info("[FCM] {} 기기로 전송 성공 (token={})", deviceType, token.getToken());
                log.debug("FCM 응답: {}", response);
            } catch (FirebaseMessagingException e) {
                log.error("[FCM] {} 기기 전송 실패 (token={}, code={})",
                        deviceType, token.getToken(), e.getMessagingErrorCode(), e);
            } catch (Exception e) {
                log.error("[FCM] 알 수 없는 오류 발생 (token={})", token.getToken(), e);
            }
        }
    }

    //모든 기기(MOBILE + WATCH) 동시 발송 + 결과 JSON 반환 + 자동 만료 토큰 삭제
    @Transactional
    public Map<String, Object> sendNotificationToAllDevices(User user, String title, String body) {
        List<FcmToken> tokens = fcmTokenRepository.findAllByUser(user);

        List<Map<String, String>> successList = new ArrayList<>();
        List<Map<String, String>> failedList = new ArrayList<>();

        if (tokens.isEmpty()) {
            log.warn("FCM] userId={} 등록된 토큰 없음", user.getId());
            return Map.of("success", successList, "failed", failedList);
        }

        for (FcmToken token : tokens) {
            Map<String, String> entry = new HashMap<>();
            entry.put("deviceType", token.getDeviceType().name());
            entry.put("token", token.getToken());

            try {
                String response = sendNotificationInternal(token.getToken(), title, body);
                entry.put("response", response);
                successList.add(entry);
                log.info("[FCM] {} 기기로 전송 성공 (token={})", token.getDeviceType(), token.getToken());
            } catch (FirebaseMessagingException e) {
                entry.put("error", e.getMessagingErrorCode().name());
                failedList.add(entry);
                log.warn("[FCM] {} 기기 전송 실패 - 만료 또는 비정상 토큰 (token={}, code={})",
                        token.getDeviceType(), token.getToken(), e.getMessagingErrorCode());
            } catch (Exception e) {
                entry.put("error", e.getMessage());
                failedList.add(entry);
                log.error("[FCM] {} 기기 전송 실패 (token={})", token.getDeviceType(), token.getToken(), e);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", successList);
        result.put("failed", failedList);
        return result;
    }
}
