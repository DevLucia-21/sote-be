package com.fluxion.sote.watch.service.impl;

import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.exception.CustomException;
import com.fluxion.sote.global.exception.ErrorCode;
import com.fluxion.sote.global.util.JwtConstants;
import com.fluxion.sote.global.util.JwtUtil;
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

    private final WatchPairCodeRepository repo;
    private final JwtUtil jwt;
    private final RedisTemplate<String, String> redis;

    private static final int CODE_LENGTH = 6;
    private static final int EXPIRE_MIN = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 시도 제한 */
    private static final String ATTEMPT_PREFIX = "watch:pair:attempt:";
    private static final long ATTEMPT_WINDOW_SEC = 60;
    private static final long ATTEMPT_MAX = 5;

    @Override
    public WatchPairCodeResponse createPairCodeForUser(User user) {

        if (user == null) throw new CustomException(ErrorCode.UNAUTHORIZED);

        // 오래된 코드 정리
        repo.deleteByExpiresAtBefore(LocalDateTime.now());

        String code = generateUniqueCode();
        LocalDateTime expires = LocalDateTime.now().plusMinutes(EXPIRE_MIN);

        WatchPairCode entity = WatchPairCode.builder()
                .code(code)
                .user(user)
                .expiresAt(expires)
                .used(false)
                .build();

        repo.save(entity);
        return new WatchPairCodeResponse(code, expires);
    }

    @Override
    public TokenResponse loginWithPairCode(WatchPairLoginRequest request) {

        String code = request.code();
        if (code == null || code.length() != 6)
            throw new CustomException(ErrorCode.INVALID_WATCH_PAIR_CODE);

        // --- 레이트 리밋 ---
        String key = ATTEMPT_PREFIX + code;
        Long attempts = redis.opsForValue().increment(key);

        if (attempts != null && attempts == 1L) {
            redis.expire(key, ATTEMPT_WINDOW_SEC, TimeUnit.SECONDS);
        }
        if (attempts != null && attempts > ATTEMPT_MAX) {
            throw new CustomException(ErrorCode.WATCH_PAIR_CODE_RATE_LIMIT_EXCEEDED);
        }

        // --- 코드 유효성 ---
        WatchPairCode pairCode = repo.findByCodeAndUsedFalse(code)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_WATCH_PAIR_CODE));

        if (pairCode.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new CustomException(ErrorCode.EXPIRED_WATCH_PAIR_CODE);

        User user = pairCode.getUser();
        if (user == null) throw new CustomException(ErrorCode.USER_NOT_FOUND);

        // --- 사용 처리 ---
        pairCode.setUsed(true);
        pairCode.setUsedAt(LocalDateTime.now());

        // --- 토큰 발급 ---
        String access = jwt.createAccessToken(user.getId(), user.getRole());
        String refresh = jwt.createRefreshToken(user.getId(), user.getRole());
        String jti = jwt.getJti(refresh);

        redis.opsForValue().set(
                JwtConstants.REFRESH_PREFIX + jti,
                user.getId().toString(),
                jwt.getRefreshExpiry(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(access, refresh, jwt.getAccessExpiry(), user.getId());
    }

    private String generateUniqueCode() {
        while (true) {
            String code = randomDigits(CODE_LENGTH);
            if (repo.findByCode(code).isEmpty()) return code;
        }
    }

    private String randomDigits(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(RANDOM.nextInt(10));
        return sb.toString();
    }
}
