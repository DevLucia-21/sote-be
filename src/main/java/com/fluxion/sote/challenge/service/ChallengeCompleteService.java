package com.fluxion.sote.challenge.service;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.service.LpRewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChallengeCompleteService {

    private final UserChallengeRepository userChallengeRepo;
    private final BadgeService badgeService;
    private final LpRewardService lpRewardService;

    /**
     * 전시회 버전:
     * - 같은 챌린지를 여러 번 완료 가능
     * - challengeId로 직접 조회
     * - 이미 완료 여부 확인 X
     * - 오늘 날짜 여부 확인 X
     */
    @Transactional
    public LpRewardResponse completeTodayChallenge(User user, Long challengeId) {

        // 1) 오늘 전용 제한 제거 → challengeId 직접 조회
        UserChallenge challenge = userChallengeRepo.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 찾을 수 없습니다."));

        // 2) 완료 여부 제한 제거 → 반복 완료 OK
        challenge.complete(); // completed=true, completedAt=now

        // 3) 뱃지 발급 (정상 작동)
        badgeService.checkAndAwardBadges(
                user,
                challenge.getChallenge().getEmotionType(),
                challenge.getChallenge().getCategory()
        );

        // 4) 추천에 사용된 분석 결과
        AnalysisResult baseResult = challenge.getAnalysisResult();
        if (baseResult == null) {
            throw new IllegalStateException("챌린지에 연결된 분석 결과가 없습니다.");
        }

        Diary diary = baseResult.getAnalysis().getDiary();
        if (diary == null) {
            throw new IllegalStateException("분석 결과에 연결된 일기가 없습니다.");
        }

        // 5) 음악 정보
        String title = baseResult.getSelectedTrackTitle();
        String artist = baseResult.getSelectedTrackArtist();
        String album = baseResult.getSelectedTrackAlbum();

        if (title == null || artist == null) {
            throw new IllegalStateException("선택된 음악 정보가 없습니다.");
        }

        // 6) LP 보상 정상 지급
        return lpRewardService.grantReward(
                user,
                diary,
                title,
                artist,
                album
        );
    }
}
