package com.fluxion.sote.health.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyHealthSummaryDto {
    private Long id;
    private String date;
    private Long steps;
    private Double avgHeartRate;
    private Double avgHrvRmssd;
    private Long sleepMinutes;
    private Double waterMl;
    private Double caffeineMg;
}
