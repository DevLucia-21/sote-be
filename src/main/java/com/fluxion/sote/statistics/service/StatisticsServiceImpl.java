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

    @Override
    public Object getDiaryStats(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        // ==========================
        // 1) 전체 조회
        // ==========================
        if ("overall".equalsIgnoreCase(period)) {
            long totalCount = diaryStatisticsRepository.countTotalByUserId(userId);
            return new DiaryTotalResponse((int) totalCount);
        }

        // ==========================
        // 2) 특정 월 조회 (yyyy-MM)
        // ==========================
        if (month != null && !month.isBlank()) {
            try {
                YearMonth target = YearMonth.parse(month); // ex) "2025-10"
                int year = target.getYear();
                int monthValue = target.getMonthValue();

                long count = diaryStatisticsRepository.countMonthlyByUserId(
                        userId,
                        year,
                        monthValue
                );

                return new DiaryMonthlyResponse((int) count);

            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid month format: " + month);
            }
        }

        // ==========================
        // 3) fallback: 이번 달 조회
        // ==========================
        if ("monthly".equalsIgnoreCase(period)) {
            LocalDate now = LocalDate.now();

            long monthlyCount = diaryStatisticsRepository.countMonthlyByUserId(
                    userId,
                    now.getYear(),
                    now.getMonthValue()
            );

            return new DiaryMonthlyResponse((int) monthlyCount);
        }

        throw new IllegalArgumentException("Invalid period: " + period);
    }

    @Override
    public AnalysisStatsResponse getAnalysisStats(String period) {
        Long userId = SecurityUtil.getCurrentUserId(); // 현재 로그인 유저 ID 가져오기

        if ("overall".equalsIgnoreCase(period)) {
            List<Object[]> results = analysisStatisticsRepository.countEmotionDistributionByUserId(userId);

            Map<String, Long> distribution = results.stream()
                    .collect(Collectors.toMap(
                            r -> (String) r[0],   // emotionLabel
                            r -> (Long) r[1]      // count
                    ));

            return new AnalysisStatsResponse(distribution);
        }

        throw new IllegalArgumentException("Invalid period for Analysis stats: " + period);
    }

    @Override
    public ChallengeCompletionResponse getChallengeCompletion(String period, String startDate) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"weekly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only weekly period supported for challenge completion");
        }

        // 주 시작일을 프론트에서 받아 사용
        LocalDate start;
        try {
            start = LocalDate.parse(startDate); // 예: 2025-11-03
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid start date: " + startDate);
        }

        LocalDate end = start.plusDays(6);

        long total = challengeStatisticsRepository.countWeeklyChallenges(userId, start, end);
        long completed = challengeStatisticsRepository.countWeeklyCompleted(userId, start, end);

        double rate = (total == 0) ? 0.0 : (double) completed / total;

        return new ChallengeCompletionResponse(total, completed, rate);
    }

    @Override
    public ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for emotion performance");
        }

        // 월 지정 (없으면 현재 월)
        LocalDate now = LocalDate.now();

        YearMonth targetMonth = (month != null)
                ? YearMonth.parse(month)   // 예: 2025-10
                : YearMonth.from(now);

        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        // 쿼리 호출
        List<Object[]> results =
                challengeStatisticsRepository.countMonthlyEmotionPerformance(userId, start, end);

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

    @Override
    public ChallengeBadgeResponse getChallengeBadges(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"overall".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only overall period supported for badge stats");
        }

        long count = userBadgeStatisticsRepository.countByUserId(userId);

        return new ChallengeBadgeResponse(count);
    }

    @Override
    public MusicStatsResponse getMusicStats(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for music stats");
        }

        // ==========================
        // 특정 월 조회 (yyyy-MM)
        // ==========================
        YearMonth target;
        try {
            target = YearMonth.parse(month); // ex) 2025-11
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid month format: " + month);
        }

        OffsetDateTime start = target.atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = target.atEndOfMonth().atTime(23, 59, 59).atOffset(ZoneOffset.UTC);

        // 이번 달 추천 음악 수
        long monthlyCount = musicStatisticsRepository.countMonthlyRecommendedTracks(
                userId, start, end
        );

        // 감정 → 장르 → count 매핑
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

    @Override
    public KeywordRankingResponse getKeywordRanking(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for keyword ranking");
        }

        // ================
        // 특정 월 조회
        // ================
        YearMonth target;
        try {
            target = YearMonth.parse(month);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid month format: " + month);
        }

        LocalDate start = target.atDay(1);
        LocalDate end = target.atEndOfMonth();

        List<Object[]> topKeywordsRaw =
                keywordStatisticsRepository.findTopKeywordsMonthly(userId, start, end);

        List<KeywordRankingResponse.KeywordRanking> rankings = topKeywordsRaw.stream()
                .limit(10)
                .map(row -> new KeywordRankingResponse.KeywordRanking(
                        (String) row[0], (Long) row[1]
                ))
                .toList();

        return new KeywordRankingResponse(rankings);
    }

    @Override
    public KeywordEmotionRankingResponse getKeywordEmotionRanking(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> emotionToKeywordRaw =
                keywordStatisticsRepository.findEmotionToKeyword(userId);

        Map<EmotionType, List<String>> emotionToKeywords = new HashMap<>();

        for (Object[] row : emotionToKeywordRaw) {
            EmotionType emotion = (EmotionType) row[0];
            String keyword = (String) row[1];

            emotionToKeywords
                    .computeIfAbsent(emotion, k -> new ArrayList<>())
                    .add(keyword);
        }

        return new KeywordEmotionRankingResponse(emotionToKeywords);
    }

    @Override
    public KeywordExploreResponse getKeywordExplore(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> keywordToEmotionRaw =
                keywordStatisticsRepository.findKeywordToEmotion(userId);

        Map<String, Map<EmotionType, Long>> keywordToEmotions = new HashMap<>();

        for (Object[] row : keywordToEmotionRaw) {
            String keyword = (String) row[0];
            EmotionType emotion = (EmotionType) row[1];
            Long count = (Long) row[2];

            // 혹시 모를 null 방어
            String safeKeyword = (keyword == null) ? "(unknown)" : keyword;

            keywordToEmotions.putIfAbsent(safeKeyword, new HashMap<>());
            keywordToEmotions.get(safeKeyword).put(emotion, count);
        }

        return new KeywordExploreResponse(keywordToEmotions);
    }
}
