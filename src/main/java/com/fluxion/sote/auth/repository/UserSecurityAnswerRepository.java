package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.UserSecurityAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Long> {
    Optional<UserSecurityAnswer> findByUserIdAndQuestionId(Long userId, Integer questionId);
}
