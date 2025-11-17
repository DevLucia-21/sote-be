package com.fluxion.sote.statistics.service;

import com.fluxion.sote.statistics.dto.*;

public interface StatisticsService {
    Object getDiaryStats(String period, Integer year, Integer month);  //수정
    AnalysisStatsResponse getAnalysisStats(String period);

    ChallengeCompletionResponse getChallengeCompletion(String period, Integer offset);  //수정
    ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month);
    ChallengeBadgeResponse getChallengeBadges(String period);

    MusicStatsResponse getMusicStats(String period, Integer year, Integer month);  //수정

    KeywordRankingResponse getKeywordRanking(String period, Integer year, Integer month);  //수정
    KeywordEmotionRankingResponse getKeywordEmotionRanking(String period);
    KeywordExploreResponse getKeywordExplore(String period);
}
