package com.fluxion.sote.challenge.service;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.analysis.repository.AnalysisResultRepository;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.service.LpRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChallengeCompleteService {

    private final UserChallengeRepository userChallengeRepo;
    private final BadgeService badgeService;
    private final AnalysisResultRepository analysisResultRepo;
    private final LpRewardService lpRewardService;

    /**
     * 오늘 추천된 챌린지 완료 처리 + LP 자동 보상 지급
     */
    @Transactional
    public LpRewardResponse completeTodayChallenge(User user, Long challengeId) {
        LocalDate today = LocalDate.now();

        // 1️⃣ 오늘 챌린지 검증
        UserChallenge challenge = userChallengeRepo.findByUserAndDate(user, today)
                .orElseThrow(() -> new IllegalStateException("오늘 추천된 챌린지가 없습니다."));

        if (!challenge.getChallenge().getId().equals(challengeId)) {
            throw new IllegalArgumentException("추천된 챌린지와 요청된 챌린지가 다릅니다.");
        }
        if (challenge.isCompleted()) {
            throw new IllegalStateException("이미 완료한 챌린지입니다.");
        }

        // 2️⃣ 완료 처리
        challenge.complete();

        // 3️⃣ 뱃지 발급
        badgeService.checkAndAwardBadges(
                user,
                challenge.getChallenge().getEmotionType(),
                challenge.getChallenge().getCategory()
        );

        // 4️⃣ 최신 분석 결과 조회 → selectedTrack 사용
        AnalysisResult latest = analysisResultRepo.findTopByAnalysis_User_IdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new IllegalStateException("분석 결과가 없습니다."));

        Diary diary = latest.getAnalysis().getDiary();
        if (diary == null) {
            throw new IllegalStateException("분석 결과에 연결된 일기가 없습니다.");
        }

        // 5️⃣ LP 지급
        return lpRewardService.grantReward(
                user,
                diary,
                latest.getSelectedTrackTitle(),
                latest.getSelectedTrackArtist(),
                latest.getSelectedTrackAlbum()
        );
    }
}
