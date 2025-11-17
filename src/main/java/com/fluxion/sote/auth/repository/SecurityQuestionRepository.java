package com.fluxion.sote.auth.repository;

import com.fluxion.sote.auth.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Integer> {
    Optional<SecurityQuestion> findByQuestionText(String questionText);
}
