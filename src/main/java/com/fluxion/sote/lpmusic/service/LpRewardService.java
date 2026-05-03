package com.fluxion.sote.lpmusic.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.entity.LpReward;
import com.fluxion.sote.lpmusic.repository.LpRewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LpRewardService {

    private final LpRewardRepository lpRewardRepo;
    private final SpotifyService spotifyService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * LP 보상 지급
     * - 하루 1개만 지급
     * - 이미 오늘 받은 LP가 있으면 기존 보상을 반환
     * - Spotify 조회 실패 시 기본 음악 정보로 저장
     */
    @Transactional
    public LpRewardResponse grantReward(User user, Diary diary, String title, String artist, String album) {
        LocalDate today = LocalDate.now(KST);

        LpReward existingReward = lpRewardRepo.findByUserAndRewardDate(user, today)
                .orElse(null);

        if (existingReward != null) {
            return LpRewardResponse.fromEntity(existingReward);
        }

        Map<String, String> trackInfo = Collections.emptyMap();

        try {
            trackInfo = spotifyService.searchTrack(title, artist);
        } catch (Exception e) {
            log.warn("Spotify track search failed. fallback to raw values. title={}, artist={}, error={}",
                    title, artist, e.getMessage());
        }

        LpReward reward = LpReward.builder()
                .user(user)
                .diary(diary)
                .title(getSafeValue(trackInfo, "title", title))
                .artist(getSafeValue(trackInfo, "artist", artist))
                .album(getSafeValue(trackInfo, "album", album))
                .albumImageUrl(getNullableValue(trackInfo, "albumImageUrl"))
                .playUrl(getNullableValue(trackInfo, "playUrl"))
                .recommendedAt(LocalDateTime.now())
                .rewardDate(today)
                .build();

        lpRewardRepo.save(reward);
        return LpRewardResponse.fromEntity(reward);
    }

    private String getSafeValue(Map<String, String> trackInfo, String key, String fallback) {
        if (trackInfo == null) {
            return fallback;
        }

        String value = trackInfo.get(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    private String getNullableValue(Map<String, String> trackInfo, String key) {
        if (trackInfo == null) {
            return null;
        }

        String value = trackInfo.get(key);
        return (value == null || value.isBlank()) ? null : value;
    }

    @Transactional(readOnly = true)
    public LpRewardResponse getTodayReward(User user) {
        LocalDate today = LocalDate.now(KST);

        return lpRewardRepo.findByUserAndRewardDate(user, today)
                .map(LpRewardResponse::fromEntity)
                .orElse(null);
    }

    /**
     * 권장 방식: 날짜 범위 기반 주간 LP 조회
     * 월이 걸치는 주차에서도 정확한 범위의 LP만 조회할 수 있음
     */
    @Transactional(readOnly = true)
    public List<LpRewardResponse> getWeeklyRewards(User user, LocalDate startDate, LocalDate endDate) {
        return lpRewardRepo.findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(user, startDate, endDate)
                .stream()
                .map(LpRewardResponse::fromEntity)
                .toList();
    }

    /**
     * 기존 호환 방식: year + week 기반 주간 LP 조회
     * 프론트는 가능하면 startDate/endDate 방식을 사용하는 것을 권장
     */
    @Transactional(readOnly = true)
    public List<LpRewardResponse> getWeeklyRewards(User user, int year, int week) {
        WeekFields weekFields = WeekFields.of(Locale.KOREA);

        LocalDate start = LocalDate.ofYearDay(year, 1)
                .with(weekFields.weekOfWeekBasedYear(), week)
                .with(weekFields.dayOfWeek(), 1);

        LocalDate end = start.plusDays(6);

        return getWeeklyRewards(user, start, end);
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getMonthlyRewards(User user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return lpRewardRepo.findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(user, start, end)
                .stream()
                .map(LpRewardResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getAllRewards(User user) {
        return lpRewardRepo.findAllByUserOrderByRecommendedAtDesc(user)
                .stream()
                .map(LpRewardResponse::fromEntity)
                .toList();
    }
}