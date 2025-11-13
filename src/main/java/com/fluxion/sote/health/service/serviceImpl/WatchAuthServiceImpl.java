// src/main/java/com/fluxion/sote/watch/service/impl/WatchAuthServiceImpl.java
package com.fluxion.sote.watch.service.impl;

import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.JwtConstants;
import com.fluxion.sote.global.util.JwtUtil;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.watch.dto.WatchPairCodeResponse;
import com.fluxion.sote.watch.dto.WatchPairLoginRequest;
import com.fluxion.sote.watch.entity.WatchPairCode;
import com.fluxion.sote.watch.repository.WatchPairCodeRepository;
import com.fluxion.sote.watch.service.WatchAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class WatchAuthServiceImpl implements WatchAuthService {

    private final WatchPairCodeRepository watchPairCodeRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis;

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MINUTES = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public WatchPairCodeResponse createPairCodeForCurrentUser() {
        User user = SecurityUtil.getCurrentUser();

        // 만료된 코드 정리
        watchPairCodeRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String code = generateUniqueCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_MINUTES);

        WatchPairCode entity = WatchPairCode.builder()
                .code(code)
                .user(user)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        watchPairCodeRepository.save(entity);

        return new WatchPairCodeResponse(code, expiresAt);
    }

    @Override
    public TokenResponse loginWithPairCode(WatchPairLoginRequest request) {
        WatchPairCode pairCode = watchPairCodeRepository.findByCodeAndUsedFalse(request.code())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 코드입니다."));

        if (pairCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("코드가 만료되었습니다.");
        }

        User user = pairCode.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 코드 사용 처리
        pairCode.setUsed(true);
        pairCode.setUsedAt(LocalDateTime.now());

        // AuthServiceImpl.login(...)과 동일한 토큰 발급 로직
        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());
        String jti     = jwtUtil.getJti(refresh);

        redis.opsForValue().set(
                JwtConstants.REFRESH_PREFIX + jti,
                user.getId().toString(),
                jwtUtil.getRefreshExpiry(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry(), user.getId());
    }

    private String generateUniqueCode() {
        while (true) {
            String code = randomDigits(CODE_LENGTH);
            boolean exists = watchPairCodeRepository.findByCodeAndUsedFalse(code).isPresent();
            if (!exists) {
                return code;
            }
        }
    }

    private String randomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int d = RANDOM.nextInt(10);
            sb.append(d);
        }
        return sb.toString();
    }
}
