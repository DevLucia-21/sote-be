// src/main/java/com/fluxion/sote/auth/service/AuthServiceImpl.java
package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.LoginRequest;
import com.fluxion.sote.auth.dto.SignupRequest;
import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import com.fluxion.sote.auth.repository.AuthRepository;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.auth.repository.SecurityQuestionRepository;
import com.fluxion.sote.auth.repository.UserSecurityAnswerRepository;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.JwtConstants;
import com.fluxion.sote.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository userRepo;
    private final GenreRepository genreRepo;
    private final SecurityQuestionRepository securityQuestionRepo;
    private final UserSecurityAnswerRepository userSecurityAnswerRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis; // @Primary 덕분에 자동 주입됨
    private final JavaMailSender mailSender;

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
        user.setCharacter(req.character());
        user.setMusicPreferences(Set.copyOf(genres));

        user = userRepo.save(user);

        UserSecurityAnswer usa = new UserSecurityAnswer();
        usa.setUser(user);
        usa.setQuestion(securityQuestionRepo.findById(req.questionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보안 질문입니다.")));
        usa.setAnswerEncrypted(passwordEncoder.encode(req.securityAnswer()));

        userSecurityAnswerRepo.save(usa);
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

        redis.opsForValue().set(
                JwtConstants.REFRESH_PREFIX + jti,
                user.getId().toString(),
                jwtUtil.getRefreshExpiry(),
                TimeUnit.MILLISECONDS
        );

        // userId 포함하여 반환
        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry(), user.getId());
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

        redis.opsForValue().set(
                JwtConstants.REFRESH_PREFIX + newJti,
                userId.toString(),
                jwtUtil.getRefreshExpiry(),
                TimeUnit.MILLISECONDS
        );

        // refresh 응답에도 userId 포함
        return new TokenResponse(newAccess, newRefresh, jwtUtil.getAccessExpiry(), user.getId());
    }


    @Override
    public void logout(String refreshToken) {
        String jti = jwtUtil.getJti(refreshToken);
        String key = JwtConstants.REFRESH_PREFIX + jti;

        Long ttl = redis.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl != null && ttl > 0) {
            redis.opsForValue().set(
                    JwtConstants.BLACKLIST_PREFIX + jti,
                    "logout",
                    ttl,
                    TimeUnit.MILLISECONDS
            );
            redis.delete(key);
        }
    }

    private User lookupByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
    }
}
