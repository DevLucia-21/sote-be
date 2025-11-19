package com.fluxion.sote.lpmusic.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.service.LpRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/lp")
@RequiredArgsConstructor
public class LpRewardController {

    private final LpRewardService rewardService;

    /**
     * 기존 기본 LP 정보 조회 (단순)
     */
    @GetMapping("/today")
    public ResponseEntity<LpRewardResponse> getTodayReward(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(rewardService.getTodayReward(user));
    }

    /**
     * 🟩 새로운 Rich 정보 포함된 LP 조회 엔드포인트
     * FE에서 원래 보던 감정색/음악 추천 이유/장르/커버 이미지를 모두 반환
     */
    @GetMapping("/today/detail")
    public ResponseEntity<Map<String, Object>> getTodayRewardDetail(
            @RequestAttribute("user") User user) {
        return ResponseEntity.ok(rewardService.getTodayRewardWithRichInfo(user));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<LpRewardResponse>> getWeeklyRewards(
            @RequestAttribute("user") User user,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week
    ) {
        if (year == null || week == null) {
            LocalDate now = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            year = now.getYear();
            week = now.get(weekFields.weekOfWeekBasedYear());
        }
        return ResponseEntity.ok(rewardService.getWeeklyRewards(user, year, week));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<LpRewardResponse>> getMonthlyRewards(
            @RequestAttribute("user") User user,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ResponseEntity.ok(rewardService.getMonthlyRewards(user, year, month));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LpRewardResponse>> getAllRewards(
            @RequestAttribute("user") User user) {
        return ResponseEntity.ok(rewardService.getAllRewards(user));
    }
}
