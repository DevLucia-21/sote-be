package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.Builder;

import java.time.LocalDateTime;

public record TodayChallengeStatus(
        boolean recommended,       // 오늘 챌린지 추천 여부
        boolean completed,         // 오늘 챌린지 완료 여부
        Long challengeId,          // 챌린지 ID
        String content,            // 챌린지 내용
        EmotionType emotionType,   // 감정 타입
        String category,           // 카테고리
        LocalDateTime completedAt  // 완료 시각 (null이면 아직 미완료)
) {
    @Builder
    public TodayChallengeStatus {}
}
