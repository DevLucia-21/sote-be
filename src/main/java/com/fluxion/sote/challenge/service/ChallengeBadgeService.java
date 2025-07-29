package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeBadgeResponseDto;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeBadgeService {

    private final UserChallengeRepository userChallengeRepo;

    public List<ChallengeBadgeResponseDto> getUserBadges(User user) {
        return userChallengeRepo.findByUserAndIsCompletedTrue(user).stream()
                .map(uc -> ChallengeBadgeResponseDto.builder()
                        .challengeId(uc.getChallenge().getId())
                        .content(uc.getChallenge().getContent())
                        .emotionType(uc.getChallenge().getEmotionType())
                        .completedDate(uc.getDate())
                        .build())
                .toList();
    }
}
