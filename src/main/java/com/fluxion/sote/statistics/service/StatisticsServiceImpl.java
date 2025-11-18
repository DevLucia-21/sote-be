package com.fluxion.sote.statistics.service;

import com.fluxion.sote.statistics.dto.ChallengeBadgeResponse;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.statistics.dto.*;
import com.fluxion.sote.statistics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final DiaryStatisticsRepository diaryStatisticsRepository;
    private final AnalysisStatisticsRepository analysisStatisticsRepository;
    private final ChallengeStatisticsRepository challengeStatisticsRepository;
    private final UserBadgeStatisticsRepository userBadgeStatisticsRepository;
    private final MusicStatisticsRepository musicStatisticsRepository;
    private final KeywordStatisticsRepository keywordStatisticsRepository;

    /*
     * -------------------------
     * 1) 일기 월간 + 전체
     * -------------------------
     */
    @Override
    public Object getDiaryStats(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if ("overall".equalsIgnoreCase(period)) {
            long totalCount = diaryStatisticsRepository.countTotalByUserId(userId);
            return new DiaryTotalResponse((int) totalCount);
        }

        if ("monthly".equalsIgnoreCase(period)) {
            YearMonth target = (month != null)
                    ? YearMonth.parse(month)
                    : YearMonth.from(LocalDate.now());

            long monthlyCount = diaryStatisticsRepository.countMonthlyByUserId(
                    userId,
                    target.getYear(),
                    target.getMonthValue()
            );

            return new DiaryMonthlyResponse((int) monthlyCount);
        }

        throw new IllegalArgumentException("Invalid period: " + period);
    }

    /*
     * -------------------------
     * 2) 감정 분석 (전체만 존재)
     * -------------------------
     */
    @Override
    public AnalysisStatsResponse getAnalysisStats(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if ("overall".equalsIgnoreCase(period)) {
            List<Object[]> results = analysisStatisticsRepository.countEmotionDistributionByUserId(userId);

            Map<String, Long> distribution = results.stream()
                    .collect(Collectors.toMap(
                            r -> (String) r[0],
                            r -> (Long) r[1]
                    ));

            return new AnalysisStatsResponse(distribution);
        }

        throw new IllegalArgumentException("Invalid period for Analysis stats: " + period);
    }

    /*
     * -------------------------
     * 3) 챌린지: 주간 완료율
     * -------------------------
     */
    @Override
    public ChallengeCompletionResponse getChallengeCompletion(String period, String week) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"weekly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only weekly period supported for challenge completion");
        }

        // 프론트에서 넘긴 주차 기준 날짜
        LocalDate baseDate;

        if (week != null) {
            // week = "2025-11-03" 같은 값
            baseDate = LocalDate.parse(week);
        } else {
            baseDate = LocalDate.now();
        }

        // baseDate가 포함된 "해당 주의 월요일~일요일" 구하기
        LocalDate start = baseDate.minusDays(baseDate.getDayOfWeek().getValue() - 1);
        LocalDate end = start.plusDays(6);

        long total = challengeStatisticsRepository.countWeeklyChallenges(userId, start, end);
        long completed = challengeStatisticsRepository.countWeeklyCompleted(userId, start, end);

        double rate = (total == 0) ? 0.0 : (double) completed / total;

        return new ChallengeCompletionResponse(total, completed, rate);
    }

    /*
     * -------------------------
     * 4) 챌린지: 월간 감정별 수행 현황 (정상 동작)
     * -------------------------
     */
    @Override
    public ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for emotion performance");
        }

        LocalDate now = LocalDate.now();
        YearMonth targetMonth = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(now);

        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        List<Object[]> results = challengeStatisticsRepository.countMonthlyEmotionPerformance(userId, start, end);

        Map<EmotionType, Long> emotionCounts = new EnumMap<>(EmotionType.class);
        Map<EmotionType, Long> totalCounts = new EnumMap<>(EmotionType.class);

        for (Object[] row : results) {
            EmotionType type = (EmotionType) row[0];
            Long completed = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long total = row.length > 2 && row[2] != null ? ((Number) row[2]).longValue() : 0L;

            emotionCounts.put(type, completed);
            totalCounts.put(type, total);
        }

        return new ChallengeEmotionPerformanceResponse(emotionCounts, totalCounts);
    }

    /*
     * -------------------------
     * 5) 챌린지: 전체 뱃지
     * -------------------------
     */
    @Override
    public ChallengeBadgeResponse getChallengeBadges(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"overall".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only overall period supported for badge stats");
        }

        long count = userBadgeStatisticsRepository.countByUserId(userId);
        return new ChallengeBadgeResponse(count);
    }

    /*
     * -------------------------
     * 6) 음악 월간
     * -------------------------
     */
    @Override
    public MusicStatsResponse getMusicStats(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for music stats");
        }

        YearMonth target = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(LocalDate.now());

        OffsetDateTime start = target.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = target.atEndOfMonth().atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        long monthlyCount = musicStatisticsRepository.countMonthlyRecommendedTracks(userId, start, end);

        List<Object[]> results = musicStatisticsRepository.countEmotionGenreMapping(userId);

        Map<String, Map<String, Long>> mapping = new HashMap<>();
        for (Object[] row : results) {
            String emotion = (String) row[0];
            String genre = (String) row[1];
            Long count = (Long) row[2];

            mapping.putIfAbsent(emotion, new HashMap<>());
            mapping.get(emotion).put(genre, count);
        }

        return new MusicStatsResponse(monthlyCount, mapping);
    }

    /*
     * -------------------------
     * 7) 키워드 월간 랭킹
     * -------------------------
     */
    @Override
    public KeywordRankingResponse getKeywordRanking(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        YearMonth target = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(LocalDate.now());

        LocalDate start = target.atDay(1);
        LocalDate end = target.atEndOfMonth();

        List<Object[]> topKeywordsRaw = keywordStatisticsRepository.findTopKeywordsMonthly(userId, start, end);

        List<KeywordRankingResponse.KeywordRanking> rankings = topKeywordsRaw.stream()
                .limit(10)
                .map(row -> new KeywordRankingResponse.KeywordRanking((String) row[0], (Long) row[1]))
                .toList();

        return new KeywordRankingResponse(rankings);
    }

    /*
     * -------------------------
     * 8) 키워드 감정 랭킹 (전체)
     * -------------------------
     */
    @Override
    public KeywordEmotionRankingResponse getKeywordEmotionRanking(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> emotionToKeywordRaw = keywordStatisticsRepository.findEmotionToKeyword(userId);

        Map<EmotionType, List<String>> emotionToKeywords = new HashMap<>();
        for (Object[] row : emotionToKeywordRaw) {
            EmotionType emotion = (EmotionType) row[0];
            String keyword = (String) row[1];
            emotionToKeywords.computeIfAbsent(emotion, k -> new ArrayList<>()).add(keyword);
        }

        return new KeywordEmotionRankingResponse(emotionToKeywords);
    }

    /*
     * -------------------------
     * 9) 키워드 탐색 (전체)
     * -------------------------
     */
    @Override
    public KeywordExploreResponse getKeywordExplore(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> keywordToEmotionRaw = keywordStatisticsRepository.findKeywordToEmotion(userId);

        Map<String, Map<EmotionType, Long>> keywordToEmotions = new HashMap<>();
        for (Object[] row : keywordToEmotionRaw) {
            String keyword = (String) row[0];
            EmotionType emotion = (EmotionType) row[1];
            Long count = (Long) row[2];

            String safeKeyword = (keyword == null) ? "(unknown)" : keyword;

            keywordToEmotions.putIfAbsent(safeKeyword, new HashMap<>());
            keywordToEmotions.get(safeKeyword).put(emotion, count);
        }

        return new KeywordExploreResponse(keywordToEmotions);
    }

}
