package com.fluxion.sote.challenge.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.dto.TodayChallengeStatus;
import com.fluxion.sote.challenge.repository.UserChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ChallengeStatusService {

    private final UserChallengeRepository userChallengeRepo;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /**
     * 오늘 챌린지 추천 여부 및 완료 여부 조회
     */
    @Transactional(readOnly = true)
    public TodayChallengeStatus getTodayStatus(User user) {
        return getStatus(user, null);
    }

    /**
     * 특정 날짜의 챌린지 추천 여부 및 완료 여부 조회
     * date가 null이면 KST 기준 오늘 날짜로 조회
     */
    @Transactional(readOnly = true)
    public TodayChallengeStatus getStatus(User user, LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now(KST);

        return userChallengeRepo.findByUserAndDate(user, targetDate)
                .map(ch -> TodayChallengeStatus.builder()
                        .recommended(true)
                        .completed(ch.isCompleted())
                        .challengeId(ch.getChallenge().getId())
                        .content(ch.getChallenge().getContent())
                        .emotionType(ch.getChallenge().getEmotionType())
                        .category(ch.getChallenge().getCategory())
                        .completedAt(ch.getCompletedAt())
                        .build())
                .orElseGet(() -> TodayChallengeStatus.builder()
                        .recommended(false)
                        .completed(false)
                        .build());
    }
}