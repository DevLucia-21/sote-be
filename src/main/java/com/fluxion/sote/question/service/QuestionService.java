// src/main/java/com/fluxion/sote/question/service/QuestionService.java
package com.fluxion.sote.question.service;

import com.fluxion.sote.question.dto.QuestionDto;
import java.util.List;

public interface QuestionService {
    List<QuestionDto> getAllQuestions();
    QuestionDto getQuestionById(Long id);
    QuestionDto createQuestion(QuestionDto dto);
    QuestionDto updateQuestion(Long id, QuestionDto dto);
    void deleteQuestion(Long id);

    // 오늘의 질문
    QuestionDto getTodayQuestion();
}
