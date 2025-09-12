package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class AnalysisStatsResponse {
    private Map<String, Long> emotionDistribution;
}
