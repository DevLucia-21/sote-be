package com.fluxion.sote.challenge.controller;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.TodayChallengeStatus;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.challenge.service.ChallengeCompleteService;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/challenge")
@RequiredArgsConstructor
public class ChallengeCompleteController {

    private final ChallengeCompleteService completeService;
    private final UserChallengeRepository userChallengeRepo;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 챌린지 완료 처리
     * 완료 후 오늘 상태 + LP 보상 정보 반환
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<TodayChallengeStatus> completeChallenge(
            @PathVariable("id") Long challengeId,
            @RequestAttribute("user") User user
    ) {
        // LP 보상 지급 및 완료 처리
        LpRewardResponse reward = completeService.completeTodayChallenge(user, challengeId);

        // 오늘 챌린지 상태 조회
        UserChallenge todayChallenge = userChallengeRepo.findByUserAndDate(user, LocalDate.now(KST))
                .orElseThrow(() -> new IllegalStateException("오늘 추천된 챌린지가 없습니다."));

        TodayChallengeStatus dto = TodayChallengeStatus.builder()
                .recommended(true)
                .completed(todayChallenge.isCompleted())
                .challengeId(todayChallenge.getChallenge().getId())
                .content(todayChallenge.getChallenge().getContent())
                .emotionType(todayChallenge.getChallenge().getEmotionType())
                .category(todayChallenge.getChallenge().getCategory())
                .completedAt(todayChallenge.getCompletedAt())
                .reward(reward)
                .build();

        return ResponseEntity.ok(dto);
    }
}
