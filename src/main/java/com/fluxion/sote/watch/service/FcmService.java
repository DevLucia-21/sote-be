package com.fluxion.sote.watch.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class FcmService {

    private static final Logger log = LoggerFactory.getLogger(FcmService.class);

    /**
     * 단일 기기 토큰으로 FCM 알림 전송
     *
     * @param token 대상 기기의 FCM 토큰
     * @param title 알림 제목
     * @param body  알림 내용
     * @param data  추가 data payload (null 가능)
     */
    public void sendToToken(String token,
                            String title,
                            String body,
                            Map<String, String> data) {

        if (data == null) {
            data = Collections.emptyMap();
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .build()
                    )
                    .putAllData(data)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 메시지 전송 성공. token={}, response={}", token, response);

        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패. token=" + token, e);
        }
    }
}
