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
     * 전시회 버전 (제한 없음)
     * - 하루 여러 번 추천 가능
     * - 최근 3일 제외 로직 없음
     */
    public ChallengeDefinition recommendTodayChallenge(User user, EmotionType emotionType) {
        LocalDate today = LocalDate.now();

        // 1. 최신 분석 결과 가져오기
        AnalysisResult latestResult = analysisResultRepo
                .findTopByAnalysis_User_IdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalStateException("최근 분석 결과가 없습니다."));

        // 2. emotionType이 null이면 분석 결과 기반
        if (emotionType == null) {
            emotionType = EmotionType.fromLabel(latestResult.getEmotionLabel());
            if (emotionType == null) {
                throw new IllegalStateException("알 수 없는 감정 라벨: " + latestResult.getEmotionLabel());
            }
        }

        // 3. 추천 후보 전체 가져오기 (제한 없음)
        List<ChallengeDefinition> candidates =
                definitionRepo.findAllByEmotionTypeAndIsDeletedFalse(emotionType);

        if (candidates.isEmpty()) {
            throw new IllegalStateException("추천 가능한 챌린지가 없습니다.");
        }

        // 4. 랜덤으로 선택
        ChallengeDefinition selected = candidates.get(new Random().nextInt(candidates.size()));

        // 5. 추천 기록 저장 (중복 여러 번 가능)
        UserChallenge record = UserChallenge.builder()
                .user(user)
                .challenge(selected)
                .analysisResult(latestResult)
                .date(today)
                .completed(false)
                .build();

        userChallengeRepo.save(record);
        return selected;
    }
}
