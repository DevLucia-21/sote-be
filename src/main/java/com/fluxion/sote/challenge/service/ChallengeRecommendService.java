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
import java.time.ZoneId;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeRecommendService {

    private final ChallengeDefinitionRepository definitionRepo;
    private final UserChallengeRepository userChallengeRepo;
    private final AnalysisResultRepository analysisResultRepo;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 오늘 챌린지 추천
     * - 오늘 이미 생성된 챌린지가 있으면 기존 챌린지 반환
     * - 없으면 최신 분석 결과 기반으로 새 챌린지 생성
     */
    public ChallengeDefinition recommendTodayChallenge(User user, EmotionType emotionType) {
        LocalDate today = LocalDate.now(KST);

        // 1. 오늘 이미 생성된 챌린지가 있으면 그대로 반환
        UserChallenge existing = userChallengeRepo.findByUserAndDate(user, today).orElse(null);
        if (existing != null) {
            return existing.getChallenge();
        }

        // 2. 최신 분석 결과 가져오기
        AnalysisResult latestResult = analysisResultRepo
                .findTopByAnalysis_User_IdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalStateException("최근 분석 결과가 없습니다."));

        // 3. 감정 타입 결정
        if (emotionType == null) {
            emotionType = EmotionType.fromLabel(latestResult.getEmotionLabel());
            if (emotionType == null) {
                throw new IllegalStateException("알 수 없는 감정 라벨: " + latestResult.getEmotionLabel());
            }
        }

        // 4. 후보 조회
        List<ChallengeDefinition> candidates =
                definitionRepo.findAllByEmotionTypeAndIsDeletedFalse(emotionType);

        if (candidates.isEmpty()) {
            throw new IllegalStateException("추천 가능한 챌린지가 없습니다.");
        }

        // 5. 랜덤 선택
        ChallengeDefinition selected = candidates.get(new Random().nextInt(candidates.size()));

        // 6. 저장 직전 한 번 더 확인
        existing = userChallengeRepo.findByUserAndDate(user, today).orElse(null);
        if (existing != null) {
            return existing.getChallenge();
        }

        // 7. 오늘 챌린지 저장
        UserChallenge record = UserChallenge.builder()
                .user(user)
                .challenge(selected)
                .analysisResult(latestResult)
                .date(today)
                .completed(false)
                .build();

        userChallengeRepo.saveAndFlush(record);
        return selected;
    }
}