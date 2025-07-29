package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeBadgeResponseDto;
import com.fluxion.sote.challenge.service.ChallengeBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeBadgeController {

    private final ChallengeBadgeService badgeService;

    // 영광의 흔적 (챌린지 뱃지 기록)
    @GetMapping("/badges")
    public ResponseEntity<List<ChallengeBadgeResponseDto>> getBadges(
            @RequestAttribute("user") User user
    ) {
        return ResponseEntity.ok(badgeService.getUserBadges(user));
    }
}
