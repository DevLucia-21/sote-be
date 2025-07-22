package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.*;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.repository.UserRepository;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
import com.fluxion.sote.global.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
        String access  = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String refresh = jwtUtil.createRefreshToken(user.getId(), user.getRole());
        return new TokenResponse(access, refresh, jwtUtil.getAccessExpiry());
    }

    @Override
    @Transactional(readOnly = true)
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

    /**
     * 닉네임과 보안 질문 답변이 일치하는 회원의 이메일을 조회합니다.
     *
     * @param req 닉네임과 보안 답변을 담은 DTO
     * @return 이메일을 담은 응답 DTO
     * @throws ResourceNotFoundException 조회된 회원이 없으면 예외 발생
     */
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
}
