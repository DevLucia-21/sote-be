// src/main/java/com/fluxion/sote/question/repository/QuestionRepository.java
package com.fluxion.sote.question.repository;

import com.fluxion.sote.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByQuestionDay(Integer questionDay);
}