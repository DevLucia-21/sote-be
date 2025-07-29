package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.service.ChallengeCompleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeCompleteController {

    private final ChallengeCompleteService completeService;

    // 챌린지 완료 처리
    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> completeChallenge(
            @PathVariable("id") Long challengeId,
            @RequestAttribute("user") User user
    ) {
        completeService.completeTodayChallenge(user, challengeId);
        return ResponseEntity.ok().build();
    }
}
