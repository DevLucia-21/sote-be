// src/main/java/com/fluxion/sote/question/service/impl/QuestionServiceImpl.java
package com.fluxion.sote.question.service.impl;

import com.fluxion.sote.question.dto.QuestionDto;
import com.fluxion.sote.question.entity.Question;
import com.fluxion.sote.question.repository.QuestionRepository;
import com.fluxion.sote.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository repo;

    private QuestionDto toDto(Question q) {
        return new QuestionDto(q.getId(), q.getContent(), q.getQuestionDay());
    }
    private Question toEntity(QuestionDto dto) {
        return new Question(dto.getContent(), dto.getQuestionDay());
    }
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Override
    public List<QuestionDto> getAllQuestions() {
        return repo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionDto getQuestionById(Long id) {
        Question q = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 질문 없음: " + id));
        return toDto(q);
    }

    @Override
    public QuestionDto createQuestion(QuestionDto dto) {
        Question saved = repo.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    public QuestionDto updateQuestion(Long id, QuestionDto dto) {
        Question q = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 질문 없음: " + id));
        q.setContent(dto.getContent());
        Question updated = repo.save(q);
        return toDto(updated);
    }

    @Override
    public void deleteQuestion(Long id) {
        repo.deleteById(id);
    }

    @Override
    public QuestionDto getTodayQuestion() {
        int day = LocalDate.now(KST).getDayOfMonth();
        Question q = repo.findById((long) day)
                .orElseThrow(() -> new RuntimeException("오늘의 질문이 없습니다: day=" + day));
        return toDto(q);
    }
}
