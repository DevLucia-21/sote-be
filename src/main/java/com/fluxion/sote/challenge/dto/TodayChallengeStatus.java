package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import lombok.Builder;

import java.time.LocalDateTime;

public record TodayChallengeStatus(
        boolean recommended,
        boolean completed,
        Long challengeId,
        String content,
        EmotionType emotionType,
        String category,
        LocalDateTime completedAt,
        LpRewardResponse reward  // LP 보상 정보 포함
) {
    @Builder
    public TodayChallengeStatus {}
}
