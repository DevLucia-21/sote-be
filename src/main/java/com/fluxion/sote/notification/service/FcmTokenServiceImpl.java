package com.fluxion.sote.notification.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.notification.entity.FcmToken;
import com.fluxion.sote.notification.repository.FcmTokenRepository;
import com.fluxion.sote.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenServiceImpl implements FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void saveToken(String token) {
        User user = SecurityUtil.getCurrentUser();
        boolean alreadyRegistered = fcmTokenRepository.findByToken(token).isPresent();
        if (!alreadyRegistered) {
            fcmTokenRepository.save(FcmToken.builder()
                    .token(token)
                    .user(user)
                    .build());
        }
    }

    @Override
    @Transactional
    public void deleteToken(String token) {
        fcmTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteAllTokensForCurrentUser() {
        User user = SecurityUtil.getCurrentUser();
        fcmTokenRepository.deleteAllByUser(user);
    }

    @Override
    @Transactional
    public void registerToken(Long userId, String tokenValue) {
        // 이미 등록된 토큰이면 중복 저장 방지
        boolean exists = fcmTokenRepository.existsByToken(tokenValue);
        if (exists) return;

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // FcmToken 엔티티 생성 및 저장
        FcmToken token = FcmToken.builder()
                .token(tokenValue)
                .user(user)
                .build();

        fcmTokenRepository.save(token);
    }
}
