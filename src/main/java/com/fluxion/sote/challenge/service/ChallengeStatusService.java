package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.TodayChallengeStatusDto;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChallengeStatusService {

    private final UserChallengeRepository userChallengeRepo;

    public TodayChallengeStatusDto getTodayStatus(User user) {
        return userChallengeRepo.findByUserAndDate(user, LocalDate.now())
                .map(ch -> TodayChallengeStatusDto.builder()
                        .isRecommended(true)
                        .isCompleted(ch.isCompleted())
                        .challengeId(ch.getChallenge().getId())
                        .content(ch.getChallenge().getContent())
                        .emotionType(ch.getChallenge().getEmotionType())
                        .build())
                .orElse(TodayChallengeStatusDto.builder()
                        .isRecommended(false)
                        .isCompleted(false)
                        .build());
    }
}
