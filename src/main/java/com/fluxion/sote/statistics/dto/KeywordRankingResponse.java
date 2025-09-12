package com.fluxion.sote.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class KeywordRankingResponse {

    @Getter
    @AllArgsConstructor
    public static class KeywordRanking {
        private String keyword;
        private long count;
    }

    private List<KeywordRanking> rankings; // Top 10 키워드
}
