package com.fluxion.sote.statistics.dto;

import com.fluxion.sote.global.enums.EmotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KeywordExploreResponse {
    private Map<String, Map<EmotionType, Long>> keywordToEmotions;
    // 예: "공부" → { JOY: 10, SADNESS: 3 }
}
