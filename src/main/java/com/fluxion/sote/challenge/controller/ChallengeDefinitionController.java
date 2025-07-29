package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.challenge.dto.ChallengeDefinitionRequestDto;
import com.fluxion.sote.challenge.dto.ChallengeDefinitionResponseDto;
import com.fluxion.sote.challenge.service.ChallengeDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge/definitions")
@RequiredArgsConstructor
public class ChallengeDefinitionController {

    private final ChallengeDefinitionService challengeService;

    // 등록
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody ChallengeDefinitionRequestDto dto) {
        Long id = challengeService.create(dto);
        return ResponseEntity.ok(id);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<ChallengeDefinitionResponseDto>> findAll() {
        return ResponseEntity.ok(challengeService.findAll());
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                       @RequestBody ChallengeDefinitionRequestDto dto) {
        challengeService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    // 삭제 (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        challengeService.delete(id);
        return ResponseEntity.ok().build();
    }
}
