package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Long> {

    // 특정 사용자 + 질문 ID로 보안 답변 조회
    Optional<UserSecurityAnswer> findByUserAndQuestionId(User user, Integer questionId);

    // userId + 질문 ID 기반 조회
    Optional<UserSecurityAnswer> findByUserIdAndQuestionId(Long userId, Integer questionId);

    // email + 질문 ID 기반 조회
    Optional<UserSecurityAnswer> findByUserEmailAndQuestionId(String email, Integer questionId);

    // 특정 사용자 전체 보안 답변 조회
    Optional<UserSecurityAnswer> findByUser(User user);

    // 사용자 삭제 시 보안 답변 삭제
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
