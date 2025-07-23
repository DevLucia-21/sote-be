package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.*;
import com.fluxion.sote.user.dto.FindEmailRequest;
import com.fluxion.sote.user.dto.FindEmailResponse;
import com.fluxion.sote.user.dto.FindPwdRequest;
import com.fluxion.sote.user.dto.FindPwdResponse;

/**
 * 인증 관련 비즈니스 로직을 정의하는 인터페이스입니다.
 */
public interface AuthService {

    /**
     * 회원가입을 처리합니다.
     *
     * @param req 사용자 가입 정보 DTO
     */
    void signup(SignupRequest req);

    /**
     * 로그인 후 액세스/리프레시 토큰을 발급합니다.
     *
     * @param req 로그인 요청 DTO
     * @return 토큰 응답 DTO
     */
    TokenResponse login(LoginRequest req);

    /**
     * 리프레시 토큰을 검증하고 새로운 토큰을 발급합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 새로운 토큰 응답 DTO
     */
    TokenResponse refresh(String refreshToken);

    /**
     * 리프레시 토큰을 무효화(로그아웃) 처리합니다.
     *
     * @param refreshToken 로그아웃할 리프레시 토큰
     */
    void logout(String refreshToken);
}
