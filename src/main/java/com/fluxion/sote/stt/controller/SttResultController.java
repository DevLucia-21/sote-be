package com.fluxion.sote.stt.controller;

import com.fluxion.sote.stt.dto.SttResultRequest;
import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.service.SttResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * FastAPI ↔ Spring STT 결과 저장 컨트롤러
 * - FastAPI 호출은 permitAll
 * - 사용자 호출은 JWT 인증 필요
 */
@RestController
@RequestMapping("/api/stt")
@RequiredArgsConstructor
@Slf4j
public class SttResultController {

    private final SttResultService sttResultService;

    @PostMapping("/results")
    public ResponseEntity<Long> saveSttResult(@RequestBody SttResultRequest request) {
        Long id = sttResultService.saveSttResult(request.getUserId(), request.getText());
        return ResponseEntity.ok(id);
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<SttResult> getSttResult(@PathVariable Long id) {
        return ResponseEntity.ok(sttResultService.getSttResult(id));
    }

    @PutMapping("/results/{id}")
    public ResponseEntity<Void> updateSttResult(@PathVariable Long id,
                                                @RequestBody SttResultRequest request) {
        sttResultService.updateSttResult(id, request.getText());
        return ResponseEntity.ok().build();
    }
}
