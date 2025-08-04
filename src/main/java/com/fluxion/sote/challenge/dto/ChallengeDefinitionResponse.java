package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.challenge.enums.EmotionType;
import lombok.Builder;

public record ChallengeDefinitionResponse(
        Long id,
        String content,
        EmotionType emotionType,
        String category
) {
    @Builder
    public ChallengeDefinitionResponse {}
}
