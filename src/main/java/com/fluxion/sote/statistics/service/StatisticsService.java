package com.fluxion.sote.statistics.service;

import com.fluxion.sote.statistics.dto.*;

public interface StatisticsService {
    Object getDiaryStats(String period, String month);
    AnalysisStatsResponse getAnalysisStats(String period);

    ChallengeCompletionResponse getChallengeCompletion(String period);
    ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month);
    ChallengeBadgeResponse getChallengeBadges(String period);

    MusicStatsResponse getMusicStats(String period, String month);

    KeywordRankingResponse getKeywordRanking(String period, String month);
    KeywordEmotionRankingResponse getKeywordEmotionRanking(String period);
    KeywordExploreResponse getKeywordExplore(String period);
}