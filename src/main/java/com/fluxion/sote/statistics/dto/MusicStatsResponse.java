package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class MusicStatsResponse {
    private long monthlyCount; // 월간 추천 곡 수
    private Map<String, Map<String, Long>> emotionGenreMapping;
    // 예: { "JOY": { "POP": 5, "JAZZ": 2 }, "SADNESS": { "BALLAD": 3 } }
}
