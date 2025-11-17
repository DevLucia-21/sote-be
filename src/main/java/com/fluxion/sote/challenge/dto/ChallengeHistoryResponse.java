package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.global.enums.EmotionType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record ChallengeHistoryResponse(
        Long id,
        LocalDate date,
        String content,
        EmotionType emotionType,
        String category,
        boolean completed,
        LocalDateTime completedAt
) {
    public static ChallengeHistoryResponse fromEntity(UserChallenge uc) {
        return ChallengeHistoryResponse.builder()
                .id(uc.getId())
                .date(uc.getDate())
                .content(uc.getChallenge().getContent())
                .emotionType(uc.getChallenge().getEmotionType())
                .category(uc.getChallenge().getCategory())
                .completed(uc.isCompleted())
                .completedAt(uc.getCompletedAt())
                .build();
    }
}
