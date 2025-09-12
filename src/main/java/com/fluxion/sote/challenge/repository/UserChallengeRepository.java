package com.fluxion.sote.challenge.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.global.enums.EmotionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    // 오늘의 추천 챌린지 조회
    Optional<UserChallenge> findByUserAndDate(User user, LocalDate date);

    // 최근 N일간 수행한 특정 감정 타입 챌린지 목록
    List<UserChallenge> findByUserAndChallenge_EmotionTypeAndDateAfter(
            User user,
            EmotionType emotionType,
            LocalDate afterDate
    );

    // 완료 횟수 세기 (전체)
    long countByUserAndCompletedTrue(User user);

    // 완료 횟수 세기 (감정별)
    long countByUserAndChallenge_EmotionTypeAndCompletedTrue(User user, EmotionType emotionType);

    // 완료 횟수 세기 (카테고리별)
    long countByUserAndChallenge_CategoryAndCompletedTrue(User user, String category);

}
