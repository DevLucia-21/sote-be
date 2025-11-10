package com.fluxion.sote.health.service.serviceImpl;

import com.fluxion.sote.health.converter.HealthConverter;
import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.dto.HealthSummaryResponse;
import com.fluxion.sote.health.entity.HealthData;
import com.fluxion.sote.health.repository.HealthRepository;
import com.fluxion.sote.health.service.HealthService;
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

    // ---- 공용 파서 ----
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

    // ---- 단건 멱등 저장 ----
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
            return HealthConverter.toResponse(saved);
        } catch (DataIntegrityViolationException race) {
            HealthData dedup = healthRepository.findByUserIdAndMeasuredAt(request.getUserId(), measuredAt)
                    .orElseThrow(() -> race);
            return HealthConverter.toResponse(dedup);
        }
    }

    // ---- ✅ 배치 멱등 저장 (최대 100건) ----
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
            // measuredAt 파싱 & 멱등키 확인
            LocalDateTime measuredAt = parseMeasuredAt(req.getMeasuredAt());

            // 1) 기존값 있으면 그대로
            Optional<HealthData> existing = healthRepository.findByUserIdAndMeasuredAt(req.getUserId(), measuredAt);
            if (existing.isPresent()) {
                results.add(HealthConverter.toResponse(existing.get()));
                continue;
            }

            // 2) 신규 엔티티
            HealthData entity = HealthData.builder()
                    .userId(req.getUserId())
                    .heartRate(req.getHeartRate())
                    .hrv(req.getHrv())
                    .steps(req.getSteps())
                    .measuredAt(measuredAt)
                    .build();

            try {
                HealthData saved = healthRepository.save(entity);
                results.add(HealthConverter.toResponse(saved));
            } catch (DataIntegrityViolationException race) {
                // 경합으로 유니크 충돌 시 기존 레코드 반환
                HealthData dedup = healthRepository.findByUserIdAndMeasuredAt(req.getUserId(), measuredAt)
                        .orElseThrow(() -> race);
                results.add(HealthConverter.toResponse(dedup));
            }
        }

        return results;
    }

    // ---- 조회/요약 기존 그대로 ----
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
            case "weekly"  -> LocalDate.now().minusDays(6).atStartOfDay();
            case "monthly" -> LocalDate.now().minusDays(29).atStartOfDay();
            default -> throw new IllegalArgumentException("Invalid period. Use 'weekly' or 'monthly'.");
        };

        List<Object[]> rows = healthRepository.findAveragesSinceNative(userId, startDate);

        return rows.stream().map(r -> {
            String date = r[0] == null ? null : r[0].toString(); // yyyy-MM-dd
            double avgHr   = r[1] == null ? 0 : ((Number) r[1]).doubleValue();
            double avgHrv  = r[2] == null ? 0 : ((Number) r[2]).doubleValue();
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
