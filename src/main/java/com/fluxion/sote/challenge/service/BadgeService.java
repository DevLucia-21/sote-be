package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.BadgeDefinition;
import com.fluxion.sote.challenge.entity.UserBadge;
import com.fluxion.sote.challenge.repository.BadgeDefinitionRepository;
import com.fluxion.sote.challenge.repository.UserBadgeRepository;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.global.enums.EmotionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeDefinitionRepository badgeDefinitionRepo;
    private final UserBadgeRepository userBadgeRepo;
    private final UserChallengeRepository userChallengeRepo;

    /**
     * 챌린지 완료 후 조건 충족 시 뱃지를 발급한다.
     */
    @Transactional
    public void checkAndAwardBadges(User user, EmotionType emotionType, String category) {
        // 1. 감정별 뱃지 후보 불러오기
        List<BadgeDefinition> emotionBadges = badgeDefinitionRepo.findByEmotionType(emotionType);

        // 2. 감정별 완료 횟수 카운트
        long completedEmotionCount =
                userChallengeRepo.countByUserAndChallenge_EmotionTypeAndCompletedTrue(user, emotionType);

        // 3. 감정별 조건 충족 시 뱃지 발급
        for (BadgeDefinition def : emotionBadges) {
            if (completedEmotionCount >= def.getConditionCount()) {
                awardIfNotExists(user, def);
            }
        }

        // 4. 카테고리 기반 뱃지 후보 불러오기
        List<BadgeDefinition> categoryBadges = badgeDefinitionRepo.findByCategory(category);

        // 5. 카테고리별 완료 횟수 카운트
        long completedCategoryCount =
                userChallengeRepo.countByUserAndChallenge_CategoryAndCompletedTrue(user, category);

        // 6. 카테고리 조건 충족 시 뱃지 발급
        for (BadgeDefinition def : categoryBadges) {
            if (completedCategoryCount >= def.getConditionCount()) {
                awardIfNotExists(user, def);
            }
        }

        // 7. 공용 뱃지 (emotionType=null, category=null)
        List<BadgeDefinition> commonBadges = badgeDefinitionRepo.findByEmotionTypeIsNullAndCategoryIsNull();
        long totalCompletedCount = userChallengeRepo.countByUserAndCompletedTrue(user);

        for (BadgeDefinition def : commonBadges) {
            if (totalCompletedCount >= def.getConditionCount()) {
                awardIfNotExists(user, def);
            }
        }
    }

    /**
     * 이미 발급된 적 없는 뱃지라면 새로 발급
     */
    private void awardIfNotExists(User user, BadgeDefinition def) {
        boolean alreadyHas = userBadgeRepo.existsByUserAndBadgeDefinition(user, def);
        if (!alreadyHas) {
            UserBadge newBadge = UserBadge.builder()
                    .user(user)
                    .badgeDefinition(def)
                    .awardedAt(LocalDateTime.now())
                    .build();
            userBadgeRepo.save(newBadge);
        }
    }
}
