package com.fluxion.sote.lpmusic.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.service.LpRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/lp")
@RequiredArgsConstructor
public class LpRewardController {

    private final LpRewardService rewardService;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @GetMapping("/today")
    public ResponseEntity<LpRewardResponse> getTodayReward(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(rewardService.getTodayReward(user));
    }

    /**
     * 주간 LP 조회
     *
     * 권장 방식:
     * GET /api/lp/weekly?startDate=2026-04-27&endDate=2026-05-03
     *
     * 기존 호환 방식:
     * GET /api/lp/weekly?year=2026&week=18
     */
    @GetMapping("/weekly")
    public ResponseEntity<List<LpRewardResponse>> getWeeklyRewards(
            @RequestAttribute("user") User user,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer week
    ) {
        if (startDate != null || endDate != null) {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("startDate와 endDate는 함께 전달해야 합니다.");
            }

            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("endDate는 startDate보다 빠를 수 없습니다.");
            }

            return ResponseEntity.ok(rewardService.getWeeklyRewards(user, startDate, endDate));
        }

        if (year == null || week == null) {
            LocalDate now = LocalDate.now(KST);
            WeekFields weekFields = WeekFields.of(Locale.KOREA);
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
    public ResponseEntity<List<LpRewardResponse>> getAllRewards(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(rewardService.getAllRewards(user));
    }
}