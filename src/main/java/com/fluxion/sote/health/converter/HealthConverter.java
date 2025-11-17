package com.fluxion.sote.health.converter;

import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.entity.HealthData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HealthConverter {

    public static HealthData toEntity(HealthRequest req) {
        LocalDateTime measuredAtLdt;

        String src = req.getMeasuredAt();
        if (src == null || src.isBlank()) {
            measuredAtLdt = LocalDateTime.now();
        } else {
            // Z/오프셋/로컬 모두 지원
            try {
                measuredAtLdt = java.time.OffsetDateTime.parse(src).toLocalDateTime();
            } catch (Exception e1) {
                try {
                    measuredAtLdt = java.time.ZonedDateTime.parse(src).toLocalDateTime();
                } catch (Exception e2) {
                    measuredAtLdt = LocalDateTime.parse(src, DateTimeFormatter.ISO_DATE_TIME);
                }
            }
        }

        return HealthData.builder()
                .userId(req.getUserId())
                .heartRate(req.getHeartRate())
                .hrv(req.getHrv())
                .steps(req.getSteps())
                .measuredAt(measuredAtLdt)
                .build();
    }

    public static HealthResponse toResponse(HealthData entity) {
        return HealthResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .heartRate(entity.getHeartRate())
                .hrv(entity.getHrv())
                .steps(entity.getSteps())
                .measuredAt(entity.getMeasuredAt() != null ? entity.getMeasuredAt().toString() : null)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }
}
