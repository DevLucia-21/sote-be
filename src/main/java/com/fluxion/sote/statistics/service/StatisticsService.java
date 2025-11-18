package com.fluxion.sote.statistics.service;

import com.fluxion.sote.statistics.dto.*;

public interface StatisticsService {

    // ---- 일기 ----
    Object getDiaryStats(String period, String month);

    // ---- 감정 분석 (전체 only) ----
    AnalysisStatsResponse getAnalysisStats(String period);

    // ---- 챌린지 ----
    ChallengeCompletionResponse getChallengeCompletion(String period, String week);
    ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month);
    ChallengeBadgeResponse getChallengeBadges(String period);

    // ---- 음악 (월간) ----
    MusicStatsResponse getMusicStats(String period, String month);

    // ---- 키워드 (월간/전체) ----
    KeywordRankingResponse getKeywordRanking(String period, String month);
    KeywordEmotionRankingResponse getKeywordEmotionRanking(String period);
    KeywordExploreResponse getKeywordExplore(String period);
}
