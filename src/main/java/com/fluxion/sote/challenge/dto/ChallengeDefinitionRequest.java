package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.Builder;

public record ChallengeDefinitionRequest(
        String content,
        EmotionType emotionType,
        String category
) {
    @Builder
    public ChallengeDefinitionRequest {}
}
