package com.fluxion.sote.setting.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.entity.FcmToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FCMService {

    /**
     * 단일 토큰 알림 전송
     */
    public void sendNotification(String targetToken, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 알림 발송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 알림 발송 실패", e);
        }
    }

    /**
     * 로그인된 유저의 모든 디바이스에 알림 전송
     */
    public void sendNotificationToUser(User user, String title, String body) {
        for (FcmToken token : user.getFcmTokens()) {
            sendNotification(token.getToken(), title, body);
        }
    }
}
