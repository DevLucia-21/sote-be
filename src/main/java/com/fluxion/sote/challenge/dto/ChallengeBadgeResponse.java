package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.Builder;

import java.time.LocalDate;

public record ChallengeBadgeResponse(
        Long challengeId,
        String content,
        EmotionType emotionType,
        LocalDate completedDate
) {
    @Builder
    public ChallengeBadgeResponse {}
}
