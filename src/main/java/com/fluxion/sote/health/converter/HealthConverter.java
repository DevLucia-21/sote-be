package com.fluxion.sote.health.converter;

import com.fluxion.sote.health.dto.HealthRequest;
import com.fluxion.sote.health.dto.HealthResponse;
import com.fluxion.sote.health.entity.HealthData;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HealthConverter {

    public static HealthData toEntity(HealthRequest req) {
        return HealthData.builder()
                .userId(req.getUserId())
                .heartRate(req.getHeartRate())
                .hrv(req.getHrv())
                .steps(req.getSteps())
                .measuredAt(LocalDateTime.parse(req.getMeasuredAt(), DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    public static HealthResponse toResponse(HealthData entity) {
        return HealthResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .heartRate(entity.getHeartRate())
                .hrv(entity.getHrv())
                .steps(entity.getSteps())
                .measuredAt(entity.getMeasuredAt().toString())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }
}
