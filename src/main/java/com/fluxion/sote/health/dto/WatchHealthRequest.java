// com.fluxion.sote.health.dto.WatchHealthRequest.java
package com.fluxion.sote.health.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WatchHealthRequest {
    private Double heartRate;
    private Double hrv;
    private Integer steps;
    private String measuredAt; // ISO 8601 문자열: "2025-11-13T14:23:00+09:00" 등
}
