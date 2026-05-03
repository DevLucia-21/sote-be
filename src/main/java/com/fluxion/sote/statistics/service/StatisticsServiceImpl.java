package com.fluxion.sote.statistics.service;

import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.statistics.dto.*;
import com.fluxion.sote.statistics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
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
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Override
    public Object getDiaryStats(String period, Integer year, Integer month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if ("overall".equalsIgnoreCase(period)) {
            long totalCount = diaryStatisticsRepository.countTotalByUserId(userId);
            return new DiaryTotalResponse((int) totalCount);
        } else if ("monthly".equalsIgnoreCase(period)) {
            LocalDate now = LocalDate.now(KST);
            int targetYear = (year != null) ? year : now.getYear();
            int targetMonth = (month != null) ? month : now.getMonthValue();

            long monthlyCount = diaryStatisticsRepository.countMonthlyByUserId(
                    userId,
                    targetYear,
                    targetMonth
            );
            return new DiaryMonthlyResponse((int) monthlyCount);
        }

        throw new IllegalArgumentException("Invalid period: " + period);
    }

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

    @Override
    public ChallengeCompletionResponse getChallengeCompletion(
            String period,
            Integer offset,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"weekly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only weekly period supported for challenge completion");
        }

        LocalDate start;
        LocalDate end;

        if (startDate != null || endDate != null) {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("startDate와 endDate는 함께 전달해야 합니다.");
            }

            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("endDate는 startDate보다 빠를 수 없습니다.");
            }

            start = startDate;
            end = endDate;
        } else {
            int weekOffset = (offset != null) ? offset : 0;

            end = LocalDate.now(KST).plusWeeks(weekOffset);
            start = end.minusDays(6);
        }

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

        LocalDate now = LocalDate.now(KST);
        YearMonth targetMonth = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(now);

        LocalDate start = targetMonth.atDay(1);
        LocalDate end = targetMonth.atEndOfMonth();

        List<Object[]> results = challengeStatisticsRepository.countMonthlyEmotionPerformance(userId, start, end);

        Map<EmotionType, Long> emotionCounts = new EnumMap<>(EmotionType.class);
        Map<EmotionType, Long> totalCounts = new EnumMap<>(EmotionType.class);

        for (Object[] row : results) {
            EmotionType type = toEmotionType(row[0]);
            if (type == null) {
                continue;
            }

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
    public MusicStatsResponse getMusicStats(String period, Integer year, Integer month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only monthly period supported for music stats");
        }

        LocalDate now = LocalDate.now(KST);
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        YearMonth targetMonthValue = YearMonth.of(targetYear, targetMonth);
        LocalDate start = targetMonthValue.atDay(1);
        LocalDate end = targetMonthValue.atEndOfMonth();

        long monthlyCount = musicStatisticsRepository.countMonthlyRecommendedTracks(
                userId,
                start,
                end
        );

        List<Object[]> results = musicStatisticsRepository.countMonthlyEmotionGenreMapping(
                userId,
                start,
                end
        );

        Map<String, Map<String, Long>> mapping = new HashMap<>();

        for (Object[] row : results) {
            String emotion = (String) row[0];
            String genre = (String) row[1];
            Long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            if (emotion == null || genre == null || genre.isBlank()) {
                continue;
            }

            mapping.putIfAbsent(emotion, new HashMap<>());
            mapping.get(emotion).put(genre, count);
        }

        return new MusicStatsResponse(monthlyCount, mapping);
    }

    @Override
    public KeywordRankingResponse getKeywordRanking(String period, Integer year, Integer month) {
        Long userId = SecurityUtil.getCurrentUserId();

        LocalDate now = LocalDate.now(KST);
        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        LocalDate start = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Object[]> topKeywordsRaw = keywordStatisticsRepository.findTopKeywordsMonthly(userId, start, end);

        List<KeywordRankingResponse.KeywordRanking> rankings = topKeywordsRaw.stream()
                .limit(10)
                .map(row -> new KeywordRankingResponse.KeywordRanking((String) row[0], ((Number) row[1]).longValue()))
                .toList();

        return new KeywordRankingResponse(rankings);
    }

    @Override
    public KeywordEmotionRankingResponse getKeywordEmotionRanking(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"overall".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only overall period supported for keyword emotion ranking");
        }

        List<Object[]> emotionToKeywordRaw = keywordStatisticsRepository.findEmotionToKeyword(userId);

        Map<EmotionType, List<String>> emotionToKeywords = new EnumMap<>(EmotionType.class);
        for (Object[] row : emotionToKeywordRaw) {
            EmotionType emotion = toEmotionType(row[0]);
            String keyword = row[1] != null ? (String) row[1] : null;

            if (emotion == null || keyword == null || keyword.isBlank()) {
                continue;
            }

            emotionToKeywords.computeIfAbsent(emotion, k -> new ArrayList<>()).add(keyword);
        }

        return new KeywordEmotionRankingResponse(emotionToKeywords);
    }

    @Override
    public KeywordExploreResponse getKeywordExplore(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"overall".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Only overall period supported for keyword explore");
        }

        List<Object[]> keywordToEmotionRaw = keywordStatisticsRepository.findKeywordToEmotion(userId);

        Map<String, Map<EmotionType, Long>> keywordToEmotions = new HashMap<>();
        for (Object[] row : keywordToEmotionRaw) {
            String keyword = row[0] != null ? (String) row[0] : null;
            EmotionType emotion = toEmotionType(row[1]);
            Long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;

            if (keyword == null || keyword.isBlank() || emotion == null) {
                continue;
            }

            keywordToEmotions.putIfAbsent(keyword, new EnumMap<>(EmotionType.class));
            keywordToEmotions.get(keyword).put(emotion, count);
        }

        return new KeywordExploreResponse(keywordToEmotions);
    }

    private EmotionType toEmotionType(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof EmotionType emotionType) {
            return emotionType;
        }

        if (value instanceof String str) {
            return EmotionType.valueOf(str);
        }

        throw new IllegalArgumentException("Unsupported emotion type value: " + value);
    }
}
