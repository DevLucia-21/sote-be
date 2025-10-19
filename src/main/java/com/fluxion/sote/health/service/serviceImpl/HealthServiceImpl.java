package com.fluxion.sote.health.service.serviceImpl;

import com.fluxion.sote.health.converter.HealthConverter;
import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.dto.HealthSummaryResponse;
import com.fluxion.sote.health.entity.HealthData;
import com.fluxion.sote.health.repository.HealthRepository;
import com.fluxion.sote.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HealthServiceImpl implements HealthService {

    private final HealthRepository healthRepository;

    @Override
    public HealthResponse saveHealthData(HealthRequest request) {
        HealthData entity = HealthConverter.toEntity(request);
        HealthData saved = healthRepository.save(entity);
        return HealthConverter.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthResponse getTodayHealth(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        return healthRepository.findTodayLatestData(userId, start, end)
                .map(HealthConverter::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthSummaryResponse> getSummary(Long userId, String period) {
        LocalDateTime startDate;

        switch (period.toLowerCase()) {
            case "weekly" -> startDate = LocalDate.now().minusDays(6).atStartOfDay();
            case "monthly" -> startDate = LocalDate.now().minusDays(29).atStartOfDay();
            default -> throw new IllegalArgumentException("Invalid period. Use 'weekly' or 'monthly'.");
        }

        List<Object[]> results = healthRepository.findAveragesSince(userId, startDate);

        return results.stream()
                .map(obj -> HealthSummaryResponse.builder()
                        .date(obj[0].toString())
                        .avgHeartRate((Double) obj[1])
                        .avgHrv((Double) obj[2])
                        .avgSteps((Double) obj[3])
                        .build())
                .collect(Collectors.toList());
    }
}
