package com.fluxion.sote.challenge.repository;

import com.fluxion.sote.challenge.entity.UserChallenge;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.ChallengeDefinition;
import com.fluxion.sote.challenge.enums.EmotionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    // 오늘의 추천 챌린지 존재 여부
    Optional<UserChallenge> findByUserAndDate(User user, LocalDate date);

    // 최근 N일간 수행한 챌린지 목록
    List<UserChallenge> findByUserAndChallenge_EmotionTypeAndDateAfter(User user, EmotionType emotionType, LocalDate afterDate);

    List<UserChallenge> findByUserAndIsCompletedTrue(User user);
}
