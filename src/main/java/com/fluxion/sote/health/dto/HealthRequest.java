package com.fluxion.sote.health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthRequest {
    private Long userId;
    private Double heartRate;
    private Double hrv;
    private Integer steps;
    private String measuredAt; // ISO 8601: "2025-10-19T12:00:00"
}
