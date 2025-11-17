package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChallengeStats {
    private long completed;  // 완료된 챌린지 수
    private long total;      // 전체 챌린지 수

    public double getCompletionRate() {
        if (total == 0) return 0.0;
        return Math.round(((double) completed / total) * 1000.0) / 10.0; // 소수점 1자리 %
    }
}
