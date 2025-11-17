package com.fluxion.sote.challenge.service;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.analysis.repository.AnalysisResultRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.ChallengeDefinition;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.ChallengeDefinitionRepository;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.global.enums.EmotionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeRecommendService {

    private final ChallengeDefinitionRepository definitionRepo;
    private final UserChallengeRepository userChallengeRepo;
    private final AnalysisResultRepository analysisResultRepo;

    /**
     * 오늘의 챌린지 추천
     * - emotionType이 null이면 최신 분석 결과의 emotionLabel 기반으로 결정
     * - 추천된 챌린지와 "어떤 분석 결과로 추천되었는지"를 UserChallenge에 함께 저장
     */
    public ChallengeDefinition recommendTodayChallenge(User user, EmotionType emotionType) {
        LocalDate today = LocalDate.now();

        // 이미 오늘 추천된 챌린지가 있으면 에러
        userChallengeRepo.findByUserAndDate(user, today).ifPresent(existing -> {
            throw new IllegalStateException("오늘의 챌린지는 이미 추천되었습니다.");
        });

        // 1. 최신 분석 결과 가져오기
        AnalysisResult latestResult = analysisResultRepo
                .findTopByAnalysis_User_IdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalStateException("최근 분석 결과가 없습니다."));

        // 2. emotionType이 null이면 분석 결과의 emotionLabel 사용
        if (emotionType == null) {
            emotionType = EmotionType.fromLabel(latestResult.getEmotionLabel());
            if (emotionType == null) {
                throw new IllegalStateException("알 수 없는 감정 라벨: " + latestResult.getEmotionLabel());
            }
        }

        // 3. 최근 3일 내 동일 감정 챌린지 제외
        LocalDate threeDaysAgo = today.minusDays(3);
        var recent = userChallengeRepo
                .findByUserAndChallenge_EmotionTypeAndDateAfter(user, emotionType, threeDaysAgo);

        List<Long> excludeIds = recent.stream()
                .map(rc -> rc.getChallenge().getId())
                .toList();

        // 4. 추천 후보 필터링
        List<ChallengeDefinition> candidates = definitionRepo
                .findAllByEmotionTypeAndIsDeletedFalse(emotionType)
                .stream()
                .filter(ch -> !excludeIds.contains(ch.getId()))
                .toList();

        if (candidates.isEmpty()) {
            candidates = definitionRepo.findAllByEmotionTypeAndIsDeletedFalse(emotionType);
        }

        if (candidates.isEmpty()) {
            throw new IllegalStateException("추천 가능한 챌린지가 없습니다.");
        }

        // 5. 랜덤으로 하나 선택
        ChallengeDefinition selected = candidates.get(new Random().nextInt(candidates.size()));

        // 6. 추천 이력 저장 + 어떤 분석 결과로 추천됐는지 함께 기록
        UserChallenge record = UserChallenge.builder()
                .user(user)
                .challenge(selected)
                .analysisResult(latestResult) // 여기서 연결
                .date(today)
                .completed(false)
                .build();

        userChallengeRepo.save(record);
        return selected;
    }
}
