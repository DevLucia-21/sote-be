// src/main/java/com/fluxion/sote/setting/service/FcmTokenServiceImpl.java
package com.fluxion.sote.setting.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.setting.entity.FcmToken;
import com.fluxion.sote.setting.enums.DeviceType;
import com.fluxion.sote.setting.repository.FcmTokenRepository;
import com.fluxion.sote.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    /**
     * 기본 MOBILE 타입으로 저장 (로그인 유저 기준)
     */
    @Override
    @Transactional
    public void saveToken(String token) {
        saveToken(token, DeviceType.MOBILE);
    }

    /**
     * 현재 로그인된 유저 기준으로 FCM 토큰 저장
     */
    @Override
    @Transactional
    public void saveToken(String token, DeviceType deviceType) {
        User user = SecurityUtil.getCurrentUser();

        if (token == null || token.isBlank()) {
            log.warn("[FCM] 비어있는 토큰 저장 시도 - userId={}", user.getId());
            return;
        }

        Optional<FcmToken> existingToken = fcmTokenRepository.findByToken(token);

        if (existingToken.isPresent()) {
            FcmToken fcmToken = existingToken.get();

            Long oldUserId = fcmToken.getUser() != null
                    ? fcmToken.getUser().getId()
                    : null;

            fcmToken.setUser(user);
            fcmToken.setDeviceType(deviceType);

            FcmToken savedToken = fcmTokenRepository.saveAndFlush(fcmToken);

            log.info("[FCM] 기존 토큰 사용자 갱신 완료 - tokenId={}, oldUserId={}, newUserId={}, deviceType={}",
                    savedToken.getId(), oldUserId, user.getId(), deviceType);

            log.info("[FCM] 토큰 테이블 row count={}", fcmTokenRepository.count());
            return;
        }

        FcmToken fcmToken = FcmToken.builder()
                .token(token)
                .deviceType(deviceType)
                .user(user)
                .build();

        FcmToken savedToken = fcmTokenRepository.saveAndFlush(fcmToken);

        log.info("[FCM] 신규 토큰 저장 완료 - tokenId={}, deviceType={}, userId={}",
                savedToken.getId(), deviceType, user.getId());

        log.info("[FCM] 토큰 테이블 row count={}", fcmTokenRepository.count());
    }

    /**
     * 특정 토큰 삭제
     */
    @Override
    @Transactional
    public void deleteToken(String token) {
        if (!fcmTokenRepository.existsByToken(token)) {
            log.warn("[FCM] 삭제 시도한 토큰이 존재하지 않음 - token={}", token);
            return;
        }

        fcmTokenRepository.deleteByToken(token);
        log.info("[FCM] 토큰 삭제 완료 - token={}", token);
    }

    /**
     * 현재 로그인된 유저의 모든 토큰 삭제
     */
    @Override
    @Transactional
    public void deleteAllTokensForCurrentUser() {
        User user = SecurityUtil.getCurrentUser();
        fcmTokenRepository.deleteAllByUser(user);
        log.info("[FCM] 모든 토큰 삭제 완료 - userId={}", user.getId());
    }

    /**
     * 기본 MOBILE 타입으로 등록 (특정 유저)
     */
    @Override
    @Transactional
    public void registerToken(Long userId, String tokenValue) {
        registerToken(userId, tokenValue, DeviceType.MOBILE);
    }

    /**
     * 특정 유저 기준으로 토큰 등록 (기기 타입 포함)
     */
    @Override
    @Transactional
    public void registerToken(Long userId, String tokenValue, DeviceType deviceType) {
        if (tokenValue == null || tokenValue.isBlank()) {
            log.warn("[FCM] 비어있는 토큰 등록 시도 - userId={}", userId);
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. (userId=" + userId + ")"));

        Optional<FcmToken> existingToken = fcmTokenRepository.findByToken(tokenValue);

        if (existingToken.isPresent()) {
            FcmToken fcmToken = existingToken.get();

            Long oldUserId = fcmToken.getUser() != null
                    ? fcmToken.getUser().getId()
                    : null;

            fcmToken.setUser(user);
            fcmToken.setDeviceType(deviceType);

            FcmToken savedToken = fcmTokenRepository.saveAndFlush(fcmToken);

            log.info("[FCM] 기존 유저 토큰 갱신 완료 - tokenId={}, oldUserId={}, newUserId={}, deviceType={}",
                    savedToken.getId(), oldUserId, userId, deviceType);

            log.info("[FCM] 토큰 테이블 row count={}", fcmTokenRepository.count());
            return;
        }

        FcmToken token = FcmToken.builder()
                .token(tokenValue)
                .deviceType(deviceType)
                .user(user)
                .build();

        FcmToken savedToken = fcmTokenRepository.saveAndFlush(token);

        log.info("[FCM] 유저 토큰 등록 완료 - tokenId={}, userId={}, deviceType={}",
                savedToken.getId(), userId, deviceType);

        log.info("[FCM] 토큰 테이블 row count={}", fcmTokenRepository.count());
    }
}
