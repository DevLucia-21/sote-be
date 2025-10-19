package com.fluxion.sote.health.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthSummaryResponse {
    private String date; // yyyy-MM-dd
    private Double avgHeartRate;
    private Double avgHrv;
    private Double avgSteps;
}
