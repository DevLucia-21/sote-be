// src/main/java/com/fluxion/sote/question/repository/QuestionRepository.java
package com.fluxion.sote.question.repository;

import com.fluxion.sote.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> { }
