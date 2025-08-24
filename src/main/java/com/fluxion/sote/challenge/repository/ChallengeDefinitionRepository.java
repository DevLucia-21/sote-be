package com.fluxion.sote.challenge.repository;

import com.fluxion.sote.challenge.entity.ChallengeDefinition;
import com.fluxion.sote.global.enums.EmotionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeDefinitionRepository extends JpaRepository<ChallengeDefinition, Long> {

    // 감정 유형별 챌린지 목록 조회 (추천용)
    List<ChallengeDefinition> findAllByEmotionTypeAndIsDeletedFalse(EmotionType emotionType);
}
