package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.SignupRequest;
import com.fluxion.sote.auth.dto.LoginRequest;
import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.repository.UserRepository;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final GenreRepository genreRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(
            UserRepository userRepo,
            GenreRepository genreRepo,
            BCryptPasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userRepo        = userRepo;
        this.genreRepo       = genreRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
    }

    @Override
    public void signup(SignupRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 1) 장르 엔티티 조회
        List<Genre> genres = genreRepo.findAllByIdIn(req.musicPreferences());
        if (genres.size() != req.musicPreferences().size()) {
            throw new IllegalArgumentException("유효하지 않은 음악 장르가 포함되어 있습니다.");
        }

        // 2) 유저 생성
        User user = new User();
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setNickname(req.nickname());
        user.setBirthDate(req.birthDate());
        user.setMusicPreferences(Set.copyOf(genres));
        userRepo.save(user);
    }

    public TokenResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());
        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry());
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        Long userId = jwtUtil.validateRefreshToken(refreshToken);
        User user   = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));
        String newAccess  = jwtUtil.createAccessToken(userId, user.getRole());
        String newRefresh = jwtUtil.createRefreshToken(userId, user.getRole());
        return new TokenResponse(newAccess, newRefresh, jwtUtil.getAccessExpiry());
    }

    @Override
    public void logout(String refreshToken) {
        // 필요 시 Redis 등에서 리프레시 토큰 블랙리스트 처리
    }
}
