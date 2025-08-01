package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Long> {
    Optional<UserSecurityAnswer> findByUserIdAndQuestionId(Long userId, Integer questionId);

    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
}
