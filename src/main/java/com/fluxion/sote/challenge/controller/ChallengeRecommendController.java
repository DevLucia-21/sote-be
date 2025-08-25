package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeDefinitionResponse;
import com.fluxion.sote.global.enums.EmotionType;
import com.fluxion.sote.challenge.service.ChallengeRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeRecommendController {

    private final ChallengeRecommendService recommendService;

    // 오늘의 챌린지 추천
    @GetMapping("/today")
    public ResponseEntity<ChallengeDefinitionResponse> getTodayChallenge(
            @RequestParam EmotionType emotion,
            @RequestAttribute("user") User user
    ) {
        var challenge = recommendService.recommendTodayChallenge(user, emotion);

        return ResponseEntity.ok(ChallengeDefinitionResponse.builder()
                .id(challenge.getId())
                .content(challenge.getContent())
                .emotionType(challenge.getEmotionType())
                .category(challenge.getCategory())
                .build());
    }
}
