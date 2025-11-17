package com.fluxion.sote.watch.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.repository.UserRepository;
import com.fluxion.sote.watch.entity.WatchNotificationToken;
import com.fluxion.sote.watch.repository.WatchNotificationTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class WatchNotificationService {

    private final WatchNotificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    public WatchNotificationService(WatchNotificationTokenRepository tokenRepository,
                                    UserRepository userRepository,
                                    FcmService fcmService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.fcmService = fcmService;
    }

    @Transactional
    public void registerToken(String deviceId, String fcmToken) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. id=" + currentUserId));

        String deviceType = "WATCH";

        tokenRepository.findByUserAndDeviceId(user, deviceId)
                .ifPresentOrElse(
                        existing -> existing.updateToken(fcmToken),
                        () -> tokenRepository.save(
                                new WatchNotificationToken(user, deviceId, fcmToken, deviceType)
                        )
                );
    }

    public void sendNotificationToCurrentUser(String title,
                                              String body,
                                              Map<String, String> data) {

        Long currentUserId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다. id=" + currentUserId));

        List<WatchNotificationToken> tokens = tokenRepository.findByUser(user);

        for (WatchNotificationToken token : tokens) {
            fcmService.sendToToken(token.getFcmToken(), title, body, data);
        }
    }
}
