package com.fluxion.sote.analysis.controller;

import com.fluxion.sote.analysis.config.AiClientProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiHealthController {

    private final @Qualifier("aiRestTemplate") RestTemplate aiRestTemplate;
    private final AiClientProperties props;

    /**
     * Render Free 환경에서 잠든 AI 서버를 미리 깨우기 위한 엔드포인트
     * 실패해도 프론트 기능을 막지 않도록 202 Accepted를 반환한다.
     */
    @GetMapping("/wake")
    public ResponseEntity<Void> wakeAiServer() {
        try {
            aiRestTemplate.getForEntity(props.getBaseUrl() + "/health", String.class);
            log.info("[AI Wake] AI 서버 깨우기 성공");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.warn("[AI Wake] AI 서버 깨우기 실패 또는 준비 중: {}", e.getMessage());
            return ResponseEntity.accepted().build();
        }
    }
}