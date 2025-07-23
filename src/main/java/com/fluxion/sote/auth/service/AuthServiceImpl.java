// src/main/java/com/fluxion/sote/auth/service/AuthServiceImpl.java
package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.LoginRequest;
import com.fluxion.sote.auth.dto.SignupRequest;
import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.AuthRepository;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.JwtConstants;
import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository userRepo;
    private final GenreRepository genreRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis;
    private final JavaMailSender mailSender;

    public AuthServiceImpl(
            AuthRepository userRepo,
            GenreRepository genreRepo,
            BCryptPasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RedisTemplate<String, String> redisTemplate,
            JavaMailSender mailSender
    ) {
        this.userRepo = userRepo;
        this.genreRepo = genreRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redis = redisTemplate;
        this.mailSender = mailSender;
    }

    @Override
    @Transactional
    public void signup(SignupRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        List<Genre> genres = genreRepo.findAllByIdIn(req.musicPreferences());
        if (genres.size() != req.musicPreferences().size()) {
            throw new IllegalArgumentException("유효하지 않은 음악 장르가 포함되어 있습니다.");
        }

        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname());
        user.setBirthDate(req.birthDate());
        user.setSecurityAnswer(req.securityAnswer());
        user.setMusicPreferences(Set.copyOf(genres));
        userRepo.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = lookupByEmail(req.email());
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());
        String jti     = jwtUtil.getJti(refresh);

        redis.opsForValue()
                .set(JwtConstants.REFRESH_PREFIX + jti,
                        user.getId().toString(),
                        jwtUtil.getRefreshExpiry(),
                        TimeUnit.MILLISECONDS);

        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        jwtUtil.validateRefreshToken(refreshToken);

        String oldJti = jwtUtil.getJti(refreshToken);
        Long userId   = jwtUtil.getUserIdFromRefreshToken(refreshToken);

        String key = JwtConstants.REFRESH_PREFIX + oldJti;
        Boolean exists = redis.hasKey(key);
        if (exists == null || !exists) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }
        redis.delete(key);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("유효하지 않은 사용자입니다."));

        String newAccess  = jwtUtil.createAccessToken(userId, user.getRole());
        String newRefresh = jwtUtil.createRefreshToken(userId, user.getRole());
        String newJti     = jwtUtil.getJti(newRefresh);

        redis.opsForValue()
                .set(JwtConstants.REFRESH_PREFIX + newJti,
                        userId.toString(),
                        jwtUtil.getRefreshExpiry(),
                        TimeUnit.MILLISECONDS);

        return new TokenResponse(newAccess, newRefresh, jwtUtil.getAccessExpiry());
    }

    @Override
    public void logout(String refreshToken) {
        String jti = jwtUtil.getJti(refreshToken);
        String key = JwtConstants.REFRESH_PREFIX + jti;

        Long ttl = redis.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl != null && ttl > 0) {
            redis.opsForValue()
                    .set(JwtConstants.BLACKLIST_PREFIX + jti,
                            "logout",
                            ttl,
                            TimeUnit.MILLISECONDS);
            redis.delete(key);
        }
    }

    // ================================================================
    // Helper to reduce duplication
    // ================================================================
    private User lookupByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
    }
}
