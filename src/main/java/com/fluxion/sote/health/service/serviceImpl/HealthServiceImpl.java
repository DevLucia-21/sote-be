package com.fluxion.sote.health.service.serviceImpl;

import com.fluxion.sote.health.converter.HealthConverter;
import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.dto.HealthSummaryResponse;
import com.fluxion.sote.health.entity.HealthData;
import com.fluxion.sote.health.repository.HealthRepository;
import com.fluxion.sote.health.service.HealthService;
import com.fluxion.sote.stress.service.StressService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HealthServiceImpl implements HealthService {

    private final HealthRepository healthRepository;
    private final StressService stressService;

    private static LocalDateTime parseMeasuredAt(String src) {
        if (src == null || src.isBlank()) return LocalDateTime.now();
        try {
            return OffsetDateTime.parse(src).toLocalDateTime();
        } catch (Exception e1) {
            try {
                return ZonedDateTime.parse(src).toLocalDateTime();
            } catch (Exception e2) {
                return LocalDateTime.parse(src, DateTimeFormatter.ISO_DATE_TIME);
            }
        }
    }

    @Override
    public HealthResponse saveHealthData(HealthRequest request) {
        LocalDateTime measuredAt = parseMeasuredAt(request.getMeasuredAt());

        Optional<HealthData> existing = healthRepository.findByUserIdAndMeasuredAt(request.getUserId(), measuredAt);
        if (existing.isPresent()) {
            return HealthConverter.toResponse(existing.get());
        }

        HealthData entity = HealthData.builder()
                .userId(request.getUserId())
                .heartRate(request.getHeartRate())
                .hrv(request.getHrv())
                .steps(request.getSteps())
                .measuredAt(measuredAt)
                .build();

        try {
            HealthData saved = healthRepository.save(entity);
            HealthResponse response = HealthConverter.toResponse(saved);
            if (response.getHrv() != null) {
                stressService.saveStress(response.getUserId(), response.getHrv(), response.getMeasuredAt());
            }
            return response;
        } catch (DataIntegrityViolationException race) {
            HealthData dedup = healthRepository.findByUserIdAndMeasuredAt(request.getUserId(), measuredAt)
                    .orElseThrow(() -> race);
            HealthResponse response = HealthConverter.toResponse(dedup);
            if (response.getHrv() != null) {
                stressService.saveStress(response.getUserId(), response.getHrv(), response.getMeasuredAt());
            }
            return response;
        }
    }

    @Override
    public List<HealthResponse> saveHealthDataBulk(List<HealthRequest> requests) {
        if (requests == null || requests.isEmpty()) return List.of();
        if (requests.size() > 100) {
            throw new IllegalArgumentException("Bulk size must be <= 100");
        }

        List<HealthResponse> results = new ArrayList<>(requests.size());

        for (HealthRequest req : requests) {
            if (req.getUserId() == null) {
                throw new IllegalArgumentException("userId is required");
            }
            LocalDateTime measuredAt = parseMeasuredAt(req.getMeasuredAt());
            Optional<HealthData> existing = healthRepository.findByUserIdAndMeasuredAt(req.getUserId(), measuredAt);
            if (existing.isPresent()) {
                HealthResponse response = HealthConverter.toResponse(existing.get());
                if (response.getHrv() != null) {
                    stressService.saveStress(response.getUserId(), response.getHrv(), response.getMeasuredAt());
                }
                results.add(response);
                continue;
            }

            HealthData entity = HealthData.builder()
                    .userId(req.getUserId())
                    .heartRate(req.getHeartRate())
                    .hrv(req.getHrv())
                    .steps(req.getSteps())
                    .measuredAt(measuredAt)
                    .build();

            try {
                HealthData saved = healthRepository.save(entity);
                HealthResponse response = HealthConverter.toResponse(saved);
                if (response.getHrv() != null) {
                    stressService.saveStress(response.getUserId(), response.getHrv(), response.getMeasuredAt());
                }
                results.add(response);
            } catch (DataIntegrityViolationException race) {
                HealthData dedup = healthRepository.findByUserIdAndMeasuredAt(req.getUserId(), measuredAt)
                        .orElseThrow(() -> race);
                HealthResponse response = HealthConverter.toResponse(dedup);
                if (response.getHrv() != null) {
                    stressService.saveStress(response.getUserId(), response.getHrv(), response.getMeasuredAt());
                }
                results.add(response);
            }
        }

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public HealthResponse getTodayHealth(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        return healthRepository
                .findTopByUserIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(userId, start, end)
                .map(HealthConverter::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthSummaryResponse> getSummary(Long userId, String period) {
        LocalDateTime startDate = switch (period.toLowerCase()) {
            case "weekly" -> LocalDate.now().minusDays(6).atStartOfDay();
            case "monthly" -> LocalDate.now().minusDays(29).atStartOfDay();
            default -> throw new IllegalArgumentException("Invalid period. Use 'weekly' or 'monthly'.");
        };

        List<Object[]> rows = healthRepository.findAveragesSinceNative(userId, startDate);

        return rows.stream().map(r -> {
            String date = r[0] == null ? null : r[0].toString();
            double avgHr = r[1] == null ? 0 : ((Number) r[1]).doubleValue();
            double avgHrv = r[2] == null ? 0 : ((Number) r[2]).doubleValue();
            double avgStep = r[3] == null ? 0 : ((Number) r[3]).doubleValue();

            return HealthSummaryResponse.builder()
                    .date(date)
                    .avgHeartRate(avgHr)
                    .avgHrv(avgHrv)
                    .avgSteps(avgStep)
                    .build();
        }).collect(Collectors.toList());
    }
}
