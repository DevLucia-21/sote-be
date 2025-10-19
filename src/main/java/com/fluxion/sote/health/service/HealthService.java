package com.fluxion.sote.health.service;

import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.dto.HealthSummaryResponse;

import java.util.List;

public interface HealthService {
    HealthResponse saveHealthData(HealthRequest request);
    HealthResponse getTodayHealth(Long userId);
    List<HealthSummaryResponse> getSummary(Long userId, String period);
}
