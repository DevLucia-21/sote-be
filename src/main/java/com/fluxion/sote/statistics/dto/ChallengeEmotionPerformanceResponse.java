package com.fluxion.sote.statistics.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ChallengeEmotionPerformanceResponse {
    private Map<EmotionType, Long> emotionCounts; // 감정별 완료 횟수
}
