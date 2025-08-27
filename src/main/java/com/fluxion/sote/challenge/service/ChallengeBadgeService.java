package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.ChallengeBadgeResponse;
import com.fluxion.sote.challenge.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeBadgeService {

    private final UserBadgeRepository userBadgeRepo;

    /**
     * 유저가 획득한 업적 뱃지 목록 조회 (최신순)
     */
    public List<ChallengeBadgeResponse> getUserBadges(User user) {
        return userBadgeRepo.findByUserOrderByAwardedAtDesc(user).stream()
                .map(ub -> ChallengeBadgeResponse.builder()
                        .badgeId(ub.getBadgeDefinition().getId())
                        .name(ub.getBadgeDefinition().getName())
                        .description(ub.getBadgeDefinition().getDescription())
                        .emotionType(ub.getBadgeDefinition().getEmotionType())
                        .category(ub.getBadgeDefinition().getCategory())
                        .awardedAt(ub.getAwardedAt())
                        .build()
                )
                .toList();
    }
}
