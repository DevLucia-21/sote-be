package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.TodayChallengeStatus;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChallengeStatusService {

    private final UserChallengeRepository userChallengeRepo;

    public TodayChallengeStatus getTodayStatus(User user) {
        return userChallengeRepo.findByUserAndDate(user, LocalDate.now())
                .map(ch -> TodayChallengeStatus.builder()
                        .isRecommended(true)
                        .isCompleted(ch.isCompleted())
                        .challengeId(ch.getChallenge().getId())
                        .content(ch.getChallenge().getContent())
                        .emotionType(ch.getChallenge().getEmotionType())
                        .build())
                .orElse(TodayChallengeStatus.builder()
                        .isRecommended(false)
                        .isCompleted(false)
                        .build());
    }
}
