package com.fluxion.sote.health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyHealthSyncRequest {
    private String date;        // yyyy-MM-dd
    private Long steps;
    private Double avgHeartRate;
    private Double avgHrvRmssd;
    private Long sleepMinutes;
    private Double waterMl;
    private Double caffeineMg;
}
