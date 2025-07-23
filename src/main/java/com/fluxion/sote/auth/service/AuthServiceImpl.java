package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.*;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.repository.UserRepository;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
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

    private final UserRepository userRepo;
    private final GenreRepository genreRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redis;
    private final JavaMailSender mailSender;

    public AuthServiceImpl(
            UserRepository userRepo,
            GenreRepository genreRepo,
            BCryptPasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            RedisTemplate<String, String> redisTemplate,
            JavaMailSender mailSender
    ) {
        this.userRepo        = userRepo;
        this.genreRepo       = genreRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
        this.redis           = redisTemplate;
        this.mailSender      = mailSender;
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
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // Access token
        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());

        // Refresh token with jti
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());
        String jti     = jwtUtil.getJti(refresh);

        // Store refresh jti in Redis with TTL
        redis.opsForValue()
                .set("refresh:" + jti, user.getId().toString(),
                        jwtUtil.getRefreshExpiry(), TimeUnit.MILLISECONDS);

        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        jwtUtil.validateRefreshToken(refreshToken);

        String oldJti = jwtUtil.getJti(refreshToken);
        Long userId   = jwtUtil.getUserIdFromRefreshToken(refreshToken);

        String key = "refresh:" + oldJti;
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
                .set("refresh:" + newJti, userId.toString(),
                        jwtUtil.getRefreshExpiry(), TimeUnit.MILLISECONDS);

        return new TokenResponse(newAccess, newRefresh, jwtUtil.getAccessExpiry());
    }

    @Override
    public void logout(String refreshToken) {
        String jti = jwtUtil.getJti(refreshToken);
        String key = "refresh:" + jti;

        Long ttl = redis.getExpire(key, TimeUnit.MILLISECONDS);
        if (ttl != null && ttl > 0) {
            redis.opsForValue()
                    .set("blacklist:" + jti, "logout", ttl, TimeUnit.MILLISECONDS);
            redis.delete(key);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FindEmailResponse findEmail(FindEmailRequest req) {
        String email = userRepo.findByNicknameAndSecurityAnswer(
                        req.getNickname(), req.getSecurityAnswer()
                )
                .map(User::getEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));
        return new FindEmailResponse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public FindPwdResponse findPassword(FindPwdRequest req) {
        String pwd = userRepo.findByEmailAndSecurityAnswer(
                        req.getEmail(), req.getSecurityAnswer()
                )
                .map(User::getPassword)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));
        return new FindPwdResponse(pwd);
    }

    /**
     * 이메일과 보안 답변이 일치할 때
     * 임시 비밀번호를 생성·저장하고 이메일로 전송합니다.
     */
    @Override
    @Transactional
    public void resetPasswordWithTemp(FindPwdRequest req) {
        User user = userRepo.findByEmailAndSecurityAnswer(
                        req.getEmail(), req.getSecurityAnswer()
                )
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원이 없습니다."));

        // 1) 임시 비밀번호 생성 (8문자)
        String temp = UUID.randomUUID().toString().substring(0, 8);

        // 2) DB에 해시 저장
        user.setPassword(passwordEncoder.encode(temp));
        userRepo.save(user);

        // 3) 이메일 발송
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Sote 임시 비밀번호 안내");
        msg.setText(
                "안녕하세요, Sote입니다.\n\n" +
                        "귀하의 임시 비밀번호는 다음과 같습니다:\n" +
                        temp + "\n\n" +
                        "로그인 후 반드시 비밀번호를 변경해 주세요.\n\n" +
                        "감사합니다."
        );
        mailSender.send(msg);
    }
}
