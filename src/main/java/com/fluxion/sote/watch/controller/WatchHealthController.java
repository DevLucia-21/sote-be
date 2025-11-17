// com.fluxion.sote.watch.controller.WatchHealthController.java
package com.fluxion.sote.watch.controller;

import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.dto.WatchHealthRequest;
import com.fluxion.sote.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watch/health")
@RequiredArgsConstructor
public class WatchHealthController {

    private final HealthService healthService;

    // 자동/주기적 건강 데이터 동기화
    @PostMapping("/sync")
    public ResponseEntity<List<HealthResponse>> syncHealth(@RequestBody List<WatchHealthRequest> requests) {
        Long userId = SecurityUtil.getCurrentUser().getId();

        List<HealthRequest> mappedRequests = requests.stream()
                .map(req -> {
                    HealthRequest r = new HealthRequest();
                    r.setUserId(userId);
                    r.setHeartRate(req.getHeartRate());
                    r.setHrv(req.getHrv());
                    r.setSteps(req.getSteps());
                    r.setMeasuredAt(req.getMeasuredAt());
                    return r;
                })
                .collect(Collectors.toList());

        List<HealthResponse> saved = healthService.saveHealthDataBulk(mappedRequests);
        return ResponseEntity.ok(saved);
    }

    // HRV 즉시 측정(수동 업로드)
    @PostMapping("/hrv/manual")
    public ResponseEntity<HealthResponse> uploadManual(@RequestBody WatchHealthRequest request) {
        Long userId = SecurityUtil.getCurrentUser().getId();

        HealthRequest mapped = new HealthRequest();
        mapped.setUserId(userId);
        mapped.setHeartRate(request.getHeartRate());
        mapped.setHrv(request.getHrv());
        mapped.setSteps(request.getSteps());
        mapped.setMeasuredAt(request.getMeasuredAt());

        HealthResponse response = healthService.saveHealthData(mapped);
        return ResponseEntity.ok(response);
    }
}
