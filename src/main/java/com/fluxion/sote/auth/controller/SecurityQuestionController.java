// src/main/java/com/fluxion/sote/auth/controller/SecurityQuestionController.java
package com.fluxion.sote.auth.controller;

import com.fluxion.sote.auth.dto.SecurityQuestionDto;
import com.fluxion.sote.auth.service.SecurityQuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/security-questions")
public class SecurityQuestionController {

    private final SecurityQuestionService securityQuestionService;

    public SecurityQuestionController(SecurityQuestionService securityQuestionService) {
        this.securityQuestionService = securityQuestionService;
    }

    @GetMapping
    public ResponseEntity<List<SecurityQuestionDto>> getAllSecurityQuestions() {
        List<SecurityQuestionDto> questions = securityQuestionService.findAll();
        return ResponseEntity.ok(questions);
    }
}
