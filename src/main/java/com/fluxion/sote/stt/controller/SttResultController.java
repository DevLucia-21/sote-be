package com.fluxion.sote.stt.controller;

import com.fluxion.sote.stt.dto.SttResultRequest;
import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.service.SttResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stt")
@RequiredArgsConstructor
@Slf4j
public class SttResultController {

    private final SttResultService sttResultService;

    // STT 결과 저장 (사용자별 하루 1회 제한)
    @PostMapping("/results")
    public ResponseEntity<Long> saveSttResult(@RequestBody SttResultRequest request) {
        Long id = sttResultService.saveSttResult(request.getText());
        return ResponseEntity.ok(id);
    }

    // STT 결과 단건 조회 (id 기준)
    @GetMapping("/results/{id}")
    public ResponseEntity<SttResult> getSttResult(@PathVariable Long id) {
        return ResponseEntity.ok(sttResultService.getSttResult(id));
    }

    // STT 결과 텍스트 수정 (오타 등은 언제든 수정 가능)
    @PutMapping("/results/{id}")
    public ResponseEntity<Void> updateSttResult(
            @PathVariable Long id,
            @RequestBody SttResultRequest request
    ) {
        sttResultService.updateSttResult(id, request.getText());
        return ResponseEntity.ok().build();
    }
}
