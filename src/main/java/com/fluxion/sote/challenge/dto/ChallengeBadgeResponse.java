package com.fluxion.sote.challenge.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChallengeBadgeResponse {
    private Long badgeId;            // 뱃지 정의 ID
    private String name;             // 뱃지 이름
    private String description;      // 뱃지 설명
    private EmotionType emotionType; // 감정 기반 뱃지 (null이면 카테고리/공용)
    private String category;         // 카테고리 기반 뱃지 (null이면 감정/공용)
    private LocalDateTime awardedAt; // 뱃지 획득 시각
}
