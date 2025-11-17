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
    public Object getDiaryStats(String period, Integer year, Integer month) {  //수정
        Long userId = SecurityUtil.getCurrentUserId();

        if ("overall".equalsIgnoreCase(period)) {
            long totalCount = diaryStatisticsRepository.countTotalByUserId(userId);
            return new DiaryTotalResponse((int) totalCount);
        } else if ("monthly".equalsIgnoreCase(period)) {
            LocalDate now = LocalDate.now();  // 기존 로직 유지
            int targetYear = (year != null) ? year : now.getYear();        //수정
            int targetMonth = (month != null) ? month : now.getMonthValue(); //수정
            long monthlyCount = diaryStatisticsRepository.countMonthlyByUserId(
                    userId,
                    targetYear,   //수정
                    targetMonth   //수정
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
                            r -> (String) r[0],    // emotionLabel
                            r -> (Long) r[1]       // count
                    ));

            return new AnalysisStatsResponse(distribution);
        }

        throw new IllegalArgumentException("Invalid period for Analysis stats: " + period);
    }

    @Override
    public ChallengeCompletionResponse getChallengeCompletion(String period, Integer offset) {  //수정
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"weekly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only weekly period supported for challenge completion");
        }

        int weekOffset = (offset != null) ? offset : 0;  //수정

        LocalDate end = LocalDate.now().plusWeeks(weekOffset);  //수정
        LocalDate start = end.minusDays(6); // 최근 7일  //수정

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
                ? YearMonth.parse(month) // 예: 2025-10
                : YearMonth.from(now);

        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        // 쿼리 호출
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
    public MusicStatsResponse getMusicStats(String period, Integer year, Integer month) {  //수정
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for music stats");
        }

        LocalDate now = LocalDate.now();  //수정
        int targetYear = (year != null) ? year : now.getYear();        //수정
        int targetMonth = (month != null) ? month : now.getMonthValue(); //수정
        LocalDate targetDate = LocalDate.of(targetYear, targetMonth, 1); //수정

        // 선택된 월 범위
        OffsetDateTime start = targetDate.withDayOfMonth(1).atStartOfDay().atOffset(ZoneOffset.UTC);  //수정
        OffsetDateTime end = targetDate.withDayOfMonth(targetDate.lengthOfMonth()).atTime(23, 59, 59).atOffset(ZoneOffset.UTC);  //수정

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

    @Override
    public KeywordRankingResponse getKeywordRanking(String period, Integer year, Integer month) {  //수정
        Long userId = SecurityUtil.getCurrentUserId();

        LocalDate now = LocalDate.now();  //수정
        int targetYear = (year != null) ? year : now.getYear();        //수정
        int targetMonth = (month != null) ? month : now.getMonthValue(); //수정

        LocalDate start = LocalDate.of(targetYear, targetMonth, 1);  //수정
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());  //수정

        List<Object[]> topKeywordsRaw = keywordStatisticsRepository.findTopKeywordsMonthly(userId, start, end);

        List<KeywordRankingResponse.KeywordRanking> rankings = topKeywordsRaw.stream()
                .limit(10)
                .map(row -> new KeywordRankingResponse.KeywordRanking((String) row[0], (Long) row[1]))
                .toList();

        return new KeywordRankingResponse(rankings);
    }

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

    @Override
    public KeywordExploreResponse getKeywordExplore(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> keywordToEmotionRaw = keywordStatisticsRepository.findKeywordToEmotion(userId);

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
