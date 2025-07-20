package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.*;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.UserRepository;
import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepo,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void signup(SignupRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname());
        userRepo.save(user);
    }

    @Override
    public TokenResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        String access = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());
        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry());
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        Long userId = jwtUtil.validateRefreshToken(refreshToken);
        String access = jwtUtil.createAccessToken(userId, jwtUtil.getRole(refreshToken));
        String refresh = jwtUtil.createRefreshToken(userId, jwtUtil.getRole(refreshToken));
        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry());
    }

    @Override
    public void logout(String refreshToken) {
        // 예: DB에서 refreshToken 제거 or 블랙리스트 등록
    }
}
