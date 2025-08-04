package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.challenge.enums.EmotionType;
import lombok.Builder;

public record TodayChallengeStatus(
        boolean isRecommended,
        boolean isCompleted,
        Long challengeId,
        String content,
        EmotionType emotionType
) {
    @Builder
    public TodayChallengeStatus {}
}
