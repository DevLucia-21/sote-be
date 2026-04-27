package com.fluxion.sote.challenge.service;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import com.fluxion.sote.diary.entity.Diary;
import com.fluxion.sote.lpmusic.dto.LpRewardResponse;
import com.fluxion.sote.lpmusic.service.LpRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeCompleteService {

    private final UserChallengeRepository userChallengeRepo;
    private final BadgeService badgeService;
    private final LpRewardService lpRewardService;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 프론트에서 전달한 challengeId(= ChallengeDefinition.id)를 기준으로
     * 오늘 로그인한 사용자의 오늘 챌린지를 찾아 완료 처리한다.
     *
     * LP 보상 실패가 챌린지 완료 자체를 막지 않도록 분리 처리
     */
    @Transactional
    public LpRewardResponse completeTodayChallenge(User user, Long challengeId) {

        UserChallenge challenge = userChallengeRepo.findByUserAndDate(user, LocalDate.now(KST))
                .filter(uc -> uc.getChallenge().getId().equals(challengeId))
                .orElseThrow(() -> new IllegalArgumentException("오늘 완료할 챌린지를 찾을 수 없습니다."));

        if (challenge.isCompleted()) {
            throw new IllegalArgumentException("이미 완료한 챌린지입니다.");
        }

        // 1. 챌린지 완료는 무조건 먼저 처리
        challenge.complete();

        // 2. 뱃지 지급
        badgeService.checkAndAwardBadges(
                user,
                challenge.getChallenge().getEmotionType(),
                challenge.getChallenge().getCategory()
        );

        // 3. LP 보상은 가능할 때만 시도
        try {
            AnalysisResult baseResult = challenge.getAnalysisResult();
            if (baseResult == null) {
                log.warn("LP 보상 생략 - analysisResult 없음. userId={}, challengeId={}", user.getId(), challengeId);
                return null;
            }

            if (baseResult.getAnalysis() == null || baseResult.getAnalysis().getDiary() == null) {
                log.warn("LP 보상 생략 - diary 연결 없음. userId={}, challengeId={}", user.getId(), challengeId);
                return null;
            }

            Diary diary = baseResult.getAnalysis().getDiary();

            String title = baseResult.getSelectedTrackTitle();
            String artist = baseResult.getSelectedTrackArtist();
            String album = baseResult.getSelectedTrackAlbum();

            if (title == null || title.isBlank() || artist == null || artist.isBlank()) {
                log.warn("LP 보상 생략 - 음악 정보 없음. userId={}, challengeId={}", user.getId(), challengeId);
                return null;
            }

            return lpRewardService.grantReward(
                    user,
                    diary,
                    title,
                    artist,
                    album
            );
        } catch (Exception e) {
            log.warn("LP 보상 지급 실패. 챌린지 완료는 유지. userId={}, challengeId={}, error={}",
                    user.getId(), challengeId, e.getMessage());
            return null;
        }
    }
}