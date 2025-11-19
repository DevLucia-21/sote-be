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
import java.util.*;

@Service
@RequiredArgsConstructor
public class LpRewardService {

    private final LpRewardRepository lpRewardRepo;
    private final SpotifyService spotifyService;

    /**
     * LP 자동 지급 (Spotify 기반)
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

    /**
     * 기존 단순 조회
     */
    @Transactional(readOnly = true)
    public LpRewardResponse getTodayReward(User user) {
        LocalDate today = LocalDate.now();
        return lpRewardRepo.findByUserAndRewardDate(user, today)
                .map(LpRewardResponse::fromEntity)
                .orElse(null);
    }

    /**
     * 🟩 Rich 정보 포함 (감정/추천 이유/장르/커버 등)
     * → FE가 원래 LP 카드 꾸밀 때 쓰던 데이터 그대로 제공
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTodayRewardWithRichInfo(User user) {
        LocalDate today = LocalDate.now();

        var rewardOpt = lpRewardRepo.findByUserAndRewardDate(user, today);
        if (rewardOpt.isEmpty()) return null;

        var reward = rewardOpt.get();
        var dto = LpRewardResponse.fromEntity(reward);

        // Diary → Analysis → AnalysisResult
        var diary = reward.getDiary();
        var analysis = diary != null ? diary.getAnalysis() : null;
        var result = analysis != null ? analysis.getResult() : null;

        Map<String, Object> resp = new LinkedHashMap<>();

        // 기본 LP 정보
        resp.put("lp", dto);

        // Rich 정보 추가
        if (result != null) {
            resp.put("emotionLabel", result.getEmotionLabel());
            resp.put("emotionReason", result.getEmotionReason());
            resp.put("emotionScore",
                    result.getEmotionScore() != null ? result.getEmotionScore().doubleValue() : null);
            resp.put("genre", result.getSelectedTrackGenre());
            resp.put("selectedTrackReason", result.getSelectedTrackReason());
            resp.put("trackCover", result.getSelectedTrackCoverImageUrl());
        }

        return resp;
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getWeeklyRewards(User user, int year, int week) {

        WeekFields wf = WeekFields.of(Locale.getDefault());
        LocalDate start = LocalDate.ofYearDay(year, 1)
                .with(wf.weekOfYear(), week)
                .with(wf.dayOfWeek(), 1);

        LocalDate end = start.plusDays(6);

        return lpRewardRepo
                .findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(user, start, end)
                .stream()
                .map(LpRewardResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LpRewardResponse> getMonthlyRewards(User user, int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        return lpRewardRepo
                .findAllByUserAndRewardDateBetweenOrderByRecommendedAtDesc(user, start, end)
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
