package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeDefinitionResponse;
import com.fluxion.sote.challenge.entity.ChallengeDefinition;
import com.fluxion.sote.challenge.service.ChallengeRecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeRecommendController {

    private final ChallengeRecommendService recommendService;

    /**
     * 오늘의 챌린지 추천
     * - 이미 추천된 게 있으면 기존 챌린지 반환
     * - 없으면 분석 결과 기반으로 신규 추천
     */
    @GetMapping("/today")
    public ResponseEntity<ChallengeDefinitionResponse> getTodayChallenge(
            @RequestAttribute("user") User user
    ) {
        // 두 번째 파라미터를 null로 넘김 → Service에서 최신 AnalysisResult 기반으로 emotionType 결정
        ChallengeDefinition challenge = recommendService.recommendTodayChallenge(user, null);

        return ResponseEntity.ok(ChallengeDefinitionResponse.builder()
                .id(challenge.getId())
                .content(challenge.getContent())
                .emotionType(challenge.getEmotionType())
                .category(challenge.getCategory())
                .build());
    }
}
