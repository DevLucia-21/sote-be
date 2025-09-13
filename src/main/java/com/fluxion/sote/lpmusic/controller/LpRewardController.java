package com.fluxion.sote.lpmusic.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.service.LpRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lp")
@RequiredArgsConstructor
public class LpRewardController {

    private final LpRewardService rewardService;

    @GetMapping("/today")
    public ResponseEntity<LpRewardResponse> getTodayReward(@RequestAttribute("user") User user) {
        return ResponseEntity.ok(rewardService.getTodayReward(user));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<LpRewardResponse>> getWeeklyRewards(
            @RequestAttribute("user") User user,
            @RequestParam int year,
            @RequestParam int week
    ) {
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
