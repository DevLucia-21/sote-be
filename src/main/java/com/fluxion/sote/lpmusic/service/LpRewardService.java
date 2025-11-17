package com.fluxion.sote.lpmusic.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.entity.LpReward;
import com.fluxion.sote.lpmusic.repository.LpRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LpRewardService {

    private final LpRewardRepository lpRewardRepo;
    private final SpotifyService spotifyService;

    /**
     * LP 자동 지급 (Spotify API 기반)
     */
    @Transactional
    public LpRewardResponse grantReward(User user, Diary diary, String title, String artist, String album) {
        LocalDate today = LocalDate.now();
        if (lpRewardRepo.existsByUserAndRewardDate(user, today)) {
            throw new IllegalStateException("오늘은 이미 LP 보상을 받았습니다.");
        }

        var trackInfo = spotifyService.searchTrack(title, artist);

        LpReward reward = LpReward.builder()
                .user(user)
                .diary(diary)
                .title(trackInfo.getOrDefault("title", title))
                .artist(trackInfo.getOrDefault("artist", artist))
                .album(trackInfo.getOrDefault("album", album))
                .albumImageUrl(trackInfo.getOrDefault("albumImageUrl", null))
                .playUrl(trackInfo.getOrDefault("playUrl", null))
                .recommendedAt(LocalDateTime.now())
                .rewardDate(today)
                .build();

        lpRewardRepo.save(reward);
        return LpRewardResponse.fromEntity(reward);
    }

    @Transactional(readOnly = true)
    public LpRewardResponse getTodayReward(User user) {
        LocalDate today = LocalDate.now();
        return lpRewardRepo.findByUserAndRewardDate(user, today)
                .map(LpRewardResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getWeeklyRewards(User user, int year, int week) {
        // 주차 계산 (ISO 기준)
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate start = LocalDate.ofYearDay(year, 1)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1); // 월요일 시작
        LocalDate end = start.plusDays(6);

        return lpRewardRepo.findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(user, start, end)
                .stream().map(LpRewardResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getMonthlyRewards(User user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();
        return lpRewardRepo.findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(user, start, end)
                .stream().map(LpRewardResponse::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getAllRewards(User user) {
        return lpRewardRepo.findAllByUserOrderByRecommendedAtDesc(user)
                .stream().map(LpRewardResponse::fromEntity).toList();
    }
}
