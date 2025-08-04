package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.EmotionDistributionResponse;
import com.fluxion.sote.challenge.dto.TodayChallengeStatus;
import com.fluxion.sote.challenge.service.ChallengeStatisticsService;
import com.fluxion.sote.challenge.service.ChallengeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeStatusController {

    private final ChallengeStatusService statusService;
    private final ChallengeStatisticsService statisticsService;

    /** 오늘 챌린지 추천 여부 및 완료 여부 조회
     * @param user 현재 인증된 사용자(@RequestAttribute 로 주입) */
    @GetMapping("/status")
    public ResponseEntity<TodayChallengeStatus> getTodayStatus(
            @RequestAttribute("user") User user
    ) {
        TodayChallengeStatus dto = statusService.getTodayStatus(user);
        return ResponseEntity.ok(dto);
    }

    /** 월별 감정 분포 비율 조회
     * @param month "YYYY-MM" 형식 (예: 2025-07) */
    @GetMapping("/emotion-distribution")
    public ResponseEntity<EmotionDistributionResponse> getEmotionDistribution(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String month
    ) {
        EmotionDistributionResponse resp = statisticsService.getEmotionDistribution(month);
        return ResponseEntity.ok(resp);
    }
}
