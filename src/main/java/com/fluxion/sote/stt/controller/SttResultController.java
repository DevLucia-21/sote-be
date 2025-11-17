package com.fluxion.sote.stt.controller;

import com.fluxion.sote.global.config.CurrentUser;
import com.fluxion.sote.stt.dto.SttResultRequest;
import com.fluxion.sote.stt.entity.SttResult;
import com.fluxion.sote.stt.service.SttResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

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
        SttResult r = sttResultService.getSttResult(id);
        Long me = CurrentUser.id();
        if (me == null || !r.getUserId().equals(me)) {
            throw new AccessDeniedException("본인 데이터만 조회할 수 있습니다.");
        }
        return ResponseEntity.ok(r);
    }

    @PutMapping("/results/{id}")
    public ResponseEntity<Void> updateSttResult(@PathVariable Long id,
                                                @RequestBody SttResultRequest request) {
        SttResult r = sttResultService.getSttResult(id);
        Long me = CurrentUser.id();
        if (me == null || !r.getUserId().equals(me)) {
            throw new AccessDeniedException("본인 데이터만 수정할 수 있습니다.");
        }
        sttResultService.updateSttResult(id, request.getText());
        return ResponseEntity.ok().build();
    }
}
