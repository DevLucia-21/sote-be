// src/main/java/com/fluxion/sote/auth/service/SecurityQuestionServiceImpl.java
package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.SecurityQuestion;              // DTO
import com.fluxion.sote.auth.repository.SecurityQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    private final SecurityQuestionRepository repo;

    public SecurityQuestionServiceImpl(SecurityQuestionRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<SecurityQuestion> findAll() {
        return repo.findAll().stream()
                .map(q -> new SecurityQuestion(   // DTO 생성
                        q.getId(),
                        q.getQuestionText()
                ))
                .collect(Collectors.toList());
    }
}
