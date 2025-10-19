package com.fluxion.sote.health.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HealthResponse {
    private Long id;
    private Long userId;
    private Double heartRate;
    private Double hrv;
    private Integer steps;
    private String measuredAt;
    private String createdAt;
}
