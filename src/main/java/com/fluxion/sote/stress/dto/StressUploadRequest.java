package com.fluxion.sote.stress.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class StressUploadRequest {

    // 워치에서 계산된 HRV (필수)
    private Double hrv;

    // 선택: 해당 시점 심박수 (없으면 null)
    private Double heartRate;

    // 선택: 해당 시점 걸음수 (없으면 null)
    private Integer steps;

    // 측정 시간 (ISO-8601 문자열 → LocalDateTime으로 파싱됨)
    // 예: "2025-11-17T06:30:00"
    private LocalDateTime measuredAt;
}
