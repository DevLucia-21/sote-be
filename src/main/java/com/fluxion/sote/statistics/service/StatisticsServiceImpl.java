package com.fluxion.sote.statistics.service.impl;

import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.statistics.dto.*;
import com.fluxion.sote.statistics.repository.*;
import com.fluxion.sote.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
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

    private static final ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");

    /* -----------------------------------
     * 1) 일기 (전체 + 월간)
     * ----------------------------------- */
    @Override
    public Object getDiaryStats(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if ("overall".equalsIgnoreCase(period)) {
            long total = diaryStatisticsRepository.countTotalByUserId(userId);
            return new DiaryTotalResponse((int) total);
        }

        if ("monthly".equalsIgnoreCase(period)) {
            YearMonth target = (month != null)
                    ? YearMonth.parse(month)
                    : YearMonth.from(LocalDate.now(ZONE_SEOUL));

            long count = diaryStatisticsRepository.countMonthlyByUserId(
                    userId,
                    target.getYear(),
                    target.getMonthValue()
            );

            return new DiaryMonthlyResponse((int) count);
        }

        throw new IllegalArgumentException("Invalid period: " + period);
    }

    /* -----------------------------------
     * 2) 감정 분석 (전체만)
     * ----------------------------------- */
    @Override
    public AnalysisStatsResponse getAnalysisStats(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"overall".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Analysis stats only support overall");
        }

        List<Object[]> results = analysisStatisticsRepository.countEmotionDistributionByUserId(userId);

        Map<String, Long> dist = results.stream()
                .collect(Collectors.toMap(
                        r -> (String) r[0],
                        r -> (Long) r[1]
                ));

        return new AnalysisStatsResponse(dist);
    }

    /* -----------------------------------
     * 3) 챌린지 주간 완료율 (week 이동 가능)
     * ----------------------------------- */
    @Override
    public ChallengeCompletionResponse getChallengeCompletion(String period, String week) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"weekly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Weekly only");
        }

        LocalDate base = (week != null)
                ? LocalDate.parse(week)
                : LocalDate.now(ZONE_SEOUL);

        LocalDate start = base.minusDays(base.getDayOfWeek().getValue() - 1);   // 월요일
        LocalDate end = start.plusDays(6);                                      // 일요일

        long total = challengeStatisticsRepository.countWeeklyChallenges(userId, start, end);
        long completed = challengeStatisticsRepository.countWeeklyCompleted(userId, start, end);

        double rate = (total == 0) ? 0.0 : (double) completed / total;

        return new ChallengeCompletionResponse(total, completed, rate);
    }

    /* -----------------------------------
     * 4) 챌린지 월간 감정별 수행
     * ----------------------------------- */
    @Override
    public ChallengeEmotionPerformanceResponse getChallengeEmotionPerformance(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Monthly only");
        }

        YearMonth target = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(LocalDate.now(ZONE_SEOUL));

        LocalDate start = target.atDay(1);
        LocalDate end = target.atEndOfMonth();

        List<Object[]> results =
                challengeStatisticsRepository.countMonthlyEmotionPerformance(userId, start, end);

        Map<EmotionType, Long> completedMap = new EnumMap<>(EmotionType.class);
        Map<EmotionType, Long> totalMap = new EnumMap<>(EmotionType.class);

        for (Object[] row : results) {
            EmotionType type = (EmotionType) row[0];
            Long completed = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            Long total = (row.length > 2 && row[2] != null) ? ((Number) row[2]).longValue() : 0L;

            completedMap.put(type, completed);
            totalMap.put(type, total);
        }

        return new ChallengeEmotionPerformanceResponse(completedMap, totalMap);
    }

    /* -----------------------------------
     * 5) 챌린지 전체 뱃지
     * ----------------------------------- */
    @Override
    public ChallengeBadgeResponse getChallengeBadges(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"overall".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Badges support only overall");
        }

        long count = userBadgeStatisticsRepository.countByUserId(userId);
        return new ChallengeBadgeResponse(count);
    }

    /* -----------------------------------
     * 6) 음악 월간 (OffsetDateTime + Asia/Seoul)
     * ----------------------------------- */
    @Override
    public MusicStatsResponse getMusicStats(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (!"monthly".equalsIgnoreCase(period)) {
            throw new IllegalArgumentException("Music stats only support monthly");
        }

        YearMonth target = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(LocalDate.now(ZONE_SEOUL));

        OffsetDateTime start = target.atDay(1)
                .atStartOfDay(ZONE_SEOUL)
                .toOffsetDateTime();

        OffsetDateTime end = target.atEndOfMonth()
                .atTime(23, 59, 59)
                .atZone(ZONE_SEOUL)
                .toOffsetDateTime();

        long count = musicStatisticsRepository.countMonthlyRecommendedTracks(userId, start, end);

        List<Object[]> raw = musicStatisticsRepository.countEmotionGenreMapping(userId);

        Map<String, Map<String, Long>> mapping = new HashMap<>();
        for (Object[] row : raw) {
            String emotion = (String) row[0];
            String genre = (String) row[1];
            Long c = (Long) row[2];

            mapping.putIfAbsent(emotion, new HashMap<>());
            mapping.get(emotion).put(genre, c);
        }

        return new MusicStatsResponse(count, mapping);
    }

    /* -----------------------------------
     * 7) 키워드 랭킹 (LocalDate)
     * ----------------------------------- */
    @Override
    public KeywordRankingResponse getKeywordRanking(String period, String month) {
        Long userId = SecurityUtil.getCurrentUserId();

        YearMonth target = (month != null)
                ? YearMonth.parse(month)
                : YearMonth.from(LocalDate.now(ZONE_SEOUL));

        LocalDate start = target.atDay(1);
        LocalDate end = target.atEndOfMonth();

        List<Object[]> raw = keywordStatisticsRepository.findTopKeywordsMonthly(userId, start, end);

        List<KeywordRankingResponse.KeywordRanking> list = raw.stream()
                .limit(10)
                .map(r -> new KeywordRankingResponse.KeywordRanking(
                        (String) r[0], (Long) r[1]
                ))
                .toList();

        return new KeywordRankingResponse(list);
    }

    /* -----------------------------------
     * 8) 키워드 감정 랭킹 (전체)
     * ----------------------------------- */
    @Override
    public KeywordEmotionRankingResponse getKeywordEmotionRanking(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> raw = keywordStatisticsRepository.findEmotionToKeyword(userId);

        Map<EmotionType, List<String>> map = new HashMap<>();
        for (Object[] r : raw) {
            EmotionType emotion = (EmotionType) r[0];
            String keyword = (String) r[1];
            map.computeIfAbsent(emotion, k -> new ArrayList<>()).add(keyword);
        }

        return new KeywordEmotionRankingResponse(map);
    }

    /* -----------------------------------
     * 9) 키워드 탐색 (전체)
     * ----------------------------------- */
    @Override
    public KeywordExploreResponse getKeywordExplore(String period) {
        Long userId = SecurityUtil.getCurrentUserId();

        List<Object[]> raw = keywordStatisticsRepository.findKeywordToEmotion(userId);

        Map<String, Map<EmotionType, Long>> map = new HashMap<>();
        for (Object[] r : raw) {
            String keyword = (String) r[0];
            EmotionType emotion = (EmotionType) r[1];
            Long count = (Long) r[2];

            String safe = (keyword == null) ? "(unknown)" : keyword;

            map.putIfAbsent(safe, new HashMap<>());
            map.get(safe).put(emotion, count);
        }

        return new KeywordExploreResponse(map);
    }
}
