package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.TodayChallengeStatusDto;
import com.fluxion.sote.challenge.service.ChallengeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeStatusController {

    private final ChallengeStatusService statusService;

    // 챌린지 현황 조회
    @GetMapping("/status")
    public ResponseEntity<TodayChallengeStatusDto> getTodayStatus(
            @RequestAttribute("user") User user
    ) {
        return ResponseEntity.ok(statusService.getTodayStatus(user));
    }
}
