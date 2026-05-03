package com.fluxion.sote.statistics.controller;

import com.fluxion.sote.statistics.dto.*;
import com.fluxion.sote.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 1) 일기
    @GetMapping("/diary")
    public ResponseEntity<?> getDiaryStats(
            @RequestParam String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(statisticsService.getDiaryStats(period, year, month));
    }

    // 2) 감정 분석
    @GetMapping("/analysis")
    public ResponseEntity<AnalysisStatsResponse> getAnalysisStats(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getAnalysisStats(period));
    }

    // 3) 챌린지 - 주간 완료율
    @GetMapping("/challenges/completion-rate")
    public ResponseEntity<ChallengeCompletionResponse> getChallengeCompletion(
            @RequestParam String period,

            // 기존 호환용: 이번 주 기준 offset
            @RequestParam(required = false) Integer offset,

            // 권장 방식: 선택 주차의 날짜 범위
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return ResponseEntity.ok(
                statisticsService.getChallengeCompletion(period, offset, startDate, endDate)
        );
    }

    @GetMapping("/challenges/emotion-performance")
    public ResponseEntity<ChallengeEmotionPerformanceResponse> getChallengeEmotionPerformance(
            @RequestParam String period,
            @RequestParam(required = false) String month
    ) {
        return ResponseEntity.ok(statisticsService.getChallengeEmotionPerformance(period, month));
    }

    @GetMapping("/challenges/badges")
    public ResponseEntity<ChallengeBadgeResponse> getChallengeBadges(@RequestParam String period) {
        return ResponseEntity.ok(statisticsService.getChallengeBadges(period));
    }

    // 4) 음악
    @GetMapping("/music")
    public ResponseEntity<MusicStatsResponse> getMusicStats(
            @RequestParam String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(statisticsService.getMusicStats(period, year, month));
    }

    // 5) 키워드
    @GetMapping("/keywords/ranking")
    public ResponseEntity<KeywordRankingResponse> getKeywordRanking(
            @RequestParam String period,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(statisticsService.getKeywordRanking(period, year, month));
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