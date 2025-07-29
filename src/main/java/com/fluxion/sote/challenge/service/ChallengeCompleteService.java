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

    @Transactional
    public void completeTodayChallenge(User user, Long challengeId) {
        LocalDate today = LocalDate.now();

        UserChallenge challenge = userChallengeRepo.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalStateException("오늘 추천된 챌린지가 없습니다."));

        if (!challenge.getChallenge().getId().equals(challengeId)) {
            throw new IllegalArgumentException("추천된 챌린지와 요청된 챌린지가 다릅니다.");
        }

        if (challenge.isCompleted()) {
            throw new IllegalStateException("이미 완료한 챌린지입니다.");
        }

        challenge.complete(); // 완료 처리
    }
}
