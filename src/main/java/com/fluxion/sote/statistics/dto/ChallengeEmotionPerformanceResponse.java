package com.fluxion.sote.statistics.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ChallengeEmotionPerformanceResponse {

    // 기존 완료 횟수 그대로 유지
    private Map<EmotionType, Long> emotionCounts;   // 감정별 완료 횟수

    // 전체 챌린지 수
    private Map<EmotionType, Long> totalCounts;     // 감정별 전체 챌린지 수
}