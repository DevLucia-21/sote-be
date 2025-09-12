package com.fluxion.sote.statistics.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class KeywordEmotionRankingResponse {
    private Map<EmotionType, List<String>> emotionToKeywords;
    // 예: JOY → ["친구", "여행"], SADNESS → ["비", "이별"]
}
