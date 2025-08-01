package com.fluxion.sote.user.service;

import com.fluxion.sote.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 사용자 계정 복구 및 프로필 관련 비즈니스 로직을 정의하는 인터페이스입니다.
 */
public interface UserService {

    /**
     * 닉네임과 보안 질문 답변이 일치하는 회원의 이메일을 조회합니다.
     *
     * @param req 닉네임과 보안 답변을 담은 DTO
     * @return 이메일을 담은 응답 DTO
     * @throws com.fluxion.sote.global.exception.ResourceNotFoundException
     *         조회된 회원이 없으면 예외 발생
     */
    FindEmailResponse findEmail(FindEmailRequest req);

    /**
     * 이메일과 보안 질문 답변이 일치하는 회원의 비밀번호를 조회합니다.
     *
     * @param req 이메일 및 보안 답변을 담은 DTO
     * @return 비밀번호를 담은 응답 DTO
     * @throws com.fluxion.sote.global.exception.ResourceNotFoundException
     *         조회된 회원이 없으면 예외 발생
     */
    FindPwdResponse findPassword(FindPwdRequest req);

    /**
     * 이메일과 보안 질문 답변이 일치하는 회원에게
     * 임시 비밀번호를 생성·저장하고, 이메일로 발송합니다.
     *
     * @param req 이메일 및 보안 답변 DTO
     */
    void resetPasswordWithTemp(FindPwdRequest req);

    /**
     * 사용자ID, 질문ID, 답변을 받아 보안 질문 일치 여부를 확인
     * @return 일치하면 true, 아니면 false
     */
    boolean checkSecurity(Long userId, Integer questionId, String answer);

    /**
     * 현재 인증된 사용자를 영구 삭제합니다.
     */
    void deleteCurrentUser();
}
