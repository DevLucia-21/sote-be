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

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ChallengeCompleteService {

    private final UserChallengeRepository userChallengeRepo;
    private final BadgeService badgeService;
    private final LpRewardService lpRewardService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 프론트에서 전달한 challengeId(= ChallengeDefinition.id)를 기준으로
     * 오늘 로그인한 사용자의 오늘 챌린지를 찾아 완료 처리한다.
     */
    @Transactional
    public LpRewardResponse completeTodayChallenge(User user, Long challengeId) {

        UserChallenge challenge = userChallengeRepo.findByUserAndDate(user, LocalDate.now(KST))
                .filter(uc -> uc.getChallenge().getId().equals(challengeId))
                .orElseThrow(() -> new IllegalArgumentException("오늘 완료할 챌린지를 찾을 수 없습니다."));

        if (challenge.isCompleted()) {
            throw new IllegalArgumentException("이미 완료한 챌린지입니다.");
        }

        challenge.complete();

        badgeService.checkAndAwardBadges(
                user,
                challenge.getChallenge().getEmotionType(),
                challenge.getChallenge().getCategory()
        );

        AnalysisResult baseResult = challenge.getAnalysisResult();
        if (baseResult == null) {
            throw new IllegalStateException("챌린지에 연결된 분석 결과가 없습니다.");
        }

        Diary diary = baseResult.getAnalysis().getDiary();
        if (diary == null) {
            throw new IllegalStateException("분석 결과에 연결된 일기가 없습니다.");
        }

        String title = baseResult.getSelectedTrackTitle();
        String artist = baseResult.getSelectedTrackArtist();
        String album = baseResult.getSelectedTrackAlbum();

        if (title == null || artist == null) {
            throw new IllegalStateException("선택된 음악 정보가 없습니다.");
        }

        return lpRewardService.grantReward(
                user,
                diary,
                title,
                artist,
                album
        );
    }
}