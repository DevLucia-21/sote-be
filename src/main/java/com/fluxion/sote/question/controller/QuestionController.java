// src/main/java/com/fluxion/sote/question/controller/QuestionController.java
package com.fluxion.sote.question.controller;

import com.fluxion.sote.question.dto.QuestionDto;
import com.fluxion.sote.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService service;

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAll() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getQuestionById(id));
    }

    // 생성
    @PostMapping
    public ResponseEntity<QuestionDto> create(@RequestBody QuestionDto dto) {
        return ResponseEntity.ok(service.createQuestion(dto));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> update(
            @PathVariable Long id,
            @RequestBody QuestionDto dto) {
        return ResponseEntity.ok(service.updateQuestion(id, dto));
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    // 오늘의 질문
    @GetMapping("/today")
    public ResponseEntity<QuestionDto> getToday() {
        return ResponseEntity.ok(service.getTodayQuestion());
    }


}
