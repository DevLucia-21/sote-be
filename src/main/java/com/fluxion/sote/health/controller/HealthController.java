package com.fluxion.sote.health.controller;

import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.dto.HealthSummaryResponse;
import com.fluxion.sote.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @PostMapping("/save")
    public ResponseEntity<HealthResponse> saveHealth(@RequestBody HealthRequest request) {
        HealthResponse response = healthService.saveHealthData(request);
        return ResponseEntity.ok(response);
    }

    // ✅ 신규: 배치 저장 (최대 100건)
    @PostMapping("/save/bulk")
    public ResponseEntity<List<HealthResponse>> saveHealthBulk(@RequestBody List<HealthRequest> requests) {
        List<HealthResponse> responses = healthService.saveHealthDataBulk(requests);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/today")
    public ResponseEntity<HealthResponse> getToday(@RequestParam Long userId) {
        HealthResponse response = healthService.getTodayHealth(userId);
        return response == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<List<HealthSummaryResponse>> getSummary(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "weekly") String period) {
        List<HealthSummaryResponse> list = healthService.getSummary(userId, period);
        return ResponseEntity.ok(list);
    }
}
