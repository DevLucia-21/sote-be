package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChallengeCompleteService {

    private final UserChallengeRepository userChallengeRepo;
    private final BadgeService badgeService; // 뱃지 서비스 주입

    /**
     * 오늘 추천된 챌린지를 완료 처리한다.
     * 완료 후 감정/카테고리 누적 조건을 검사하여 뱃지를 발급한다.
     */
    @Transactional
    public void completeTodayChallenge(User user, Long challengeId) {
        LocalDate today = LocalDate.now();

        // 오늘 추천된 챌린지 가져오기
        UserChallenge challenge = userChallengeRepo.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalStateException("오늘 추천된 챌린지가 없습니다."));

        // 요청한 챌린지가 오늘 추천된 챌린지가 맞는지 검증
        if (!challenge.getChallenge().getId().equals(challengeId)) {
            throw new IllegalArgumentException("추천된 챌린지와 요청된 챌린지가 다릅니다.");
        }

        // 이미 완료한 경우 예외
        if (challenge.isCompleted()) {
            throw new IllegalStateException("이미 완료한 챌린지입니다.");
        }

        // 완료 처리 (UserChallenge.complete() 내부에서 completedAt 세팅)
        challenge.complete();

        // 완료 후 뱃지 조건 검사 및 발급 (감정 + 카테고리 모두)
        badgeService.checkAndAwardBadges(
                user,
                challenge.getChallenge().getEmotionType(),
                challenge.getChallenge().getCategory()
        );
    }
}
