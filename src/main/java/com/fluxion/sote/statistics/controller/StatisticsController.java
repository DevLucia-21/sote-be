package com.fluxion.sote.statistics.controller;

import com.fluxion.sote.statistics.dto.ChallengeBadgeResponse;
import com.fluxion.sote.statistics.dto.*;
import com.fluxion.sote.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 1) 일기
    @GetMapping("/diary")
    public ResponseEntity<DiaryStatsResponse> getDiaryStats(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getDiaryStats(period));
    }

    // 2) 감정 분석
    @GetMapping("/analysis")
    public ResponseEntity<AnalysisStatsResponse> getAnalysisStats(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getAnalysisStats(period));
    }

    // 3) 챌린지
    @GetMapping("/challenges/completion-rate")
    public ResponseEntity<ChallengeCompletionResponse> getChallengeCompletion(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getChallengeCompletion(period));
    }

    @GetMapping("/challenges/emotion-performance")
    public ResponseEntity<ChallengeEmotionPerformanceResponse> getChallengeEmotionPerformance(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getChallengeEmotionPerformance(period));
    }

    @GetMapping("/challenges/badges")
    public ResponseEntity<ChallengeBadgeResponse> getChallengeBadges(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getChallengeBadges(period));
    }

    // 4) 음악
    @GetMapping("/music")
    public ResponseEntity<MusicStatsResponse> getMusicStats(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getMusicStats(period));
    }

    // 5) 키워드
    @GetMapping("/keywords/ranking")
    public ResponseEntity<KeywordRankingResponse> getKeywordRanking(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getKeywordRanking(period));
    }

    @GetMapping("/keywords/emotion-ranking")
    public ResponseEntity<KeywordEmotionRankingResponse> getKeywordEmotionRanking(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getKeywordEmotionRanking(period));
    }

    @GetMapping("/keywords/explore")
    public ResponseEntity<KeywordExploreResponse> getKeywordExplore(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getKeywordExplore(period));
    }
}
