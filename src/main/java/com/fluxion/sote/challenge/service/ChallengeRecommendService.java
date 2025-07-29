package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.ChallengeDefinition;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.enums.EmotionType;
import com.fluxion.sote.challenge.repository.ChallengeDefinitionRepository;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ChallengeRecommendService {

    private final ChallengeDefinitionRepository definitionRepo;
    private final UserChallengeRepository userChallengeRepo;

    public ChallengeDefinition recommendTodayChallenge(User user, EmotionType emotionType) {

        // 이미 오늘 추천된 챌린지가 있다면 반환
        LocalDate today = LocalDate.now();
        userChallengeRepo.findByUserAndDate(user, today).ifPresent(existing -> {
            throw new IllegalStateException("오늘의 챌린지는 이미 추천되었습니다.");
        });

        // 최근 3일 이내 완료한 챌린지 ID 수집
        LocalDate threeDaysAgo = today.minusDays(3);
        List<UserChallenge> recent = userChallengeRepo.findByUserAndChallenge_EmotionTypeAndDateAfter(user, emotionType, threeDaysAgo);
        List<Long> excludeIds = recent.stream().map(rc -> rc.getChallenge().getId()).toList();

        // 추천 후보 필터링
        List<ChallengeDefinition> candidates = definitionRepo.findAllByEmotionTypeAndIsDeletedFalse(emotionType).stream()
                .filter(ch -> !excludeIds.contains(ch.getId()))
                .toList();

        // 후보가 없으면 전체에서 fallback 랜덤
        if (candidates.isEmpty()) {
            candidates = definitionRepo.findAllByEmotionTypeAndIsDeletedFalse(emotionType);
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("추천 가능한 챌린지가 없습니다.");
        }

        // 랜덤 1개 추출
        ChallengeDefinition selected = candidates.get(new Random().nextInt(candidates.size()));

        // 추천 이력 저장 (isCompleted = false)
        UserChallenge record = UserChallenge.builder()
                .user(user)
                .challenge(selected)
                .date(today)
                .isCompleted(false)
                .build();

        userChallengeRepo.save(record);
        return selected;
    }
}
