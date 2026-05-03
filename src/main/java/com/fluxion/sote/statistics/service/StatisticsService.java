package com.fluxion.sote.statistics.service;

import com.fluxion.sote.statistics.dto.*;

import java.time.LocalDate;

public interface StatisticsService {

    Object getDiaryStats(String period, Integer year, Integer month);

    AnalysisStatsResponse getAnalysisStats(String period);

    ChallengeCompletionResponse getChallengeCompletion(
            String period,
            Integer offset,
            LocalDate startDate,
            LocalDate endDate
    );

    ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month);

    ChallengeBadgeResponse getChallengeBadges(String period);

    MusicStatsResponse getMusicStats(String period, Integer year, Integer month);

    KeywordRankingResponse getKeywordRanking(String period, Integer year, Integer month);

    KeywordEmotionRankingResponse getKeywordEmotionRanking(String period);

    KeywordExploreResponse getKeywordExplore(String period);
}