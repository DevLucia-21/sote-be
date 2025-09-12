package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeCompletionResponse {
    private long totalChallenges;   // 주간 추천된 챌린지 수
    private long completedChallenges; // 주간 완료된 챌린지 수
    private double completionRate;  // 완료율 (0.0 ~ 1.0)
}
