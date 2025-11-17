package com.fluxion.sote.challenge.repository;

import com.fluxion.sote.challenge.entity.BadgeDefinition;
import com.fluxion.sote.global.enums.EmotionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeDefinitionRepository extends JpaRepository<BadgeDefinition, Long> {

    // 특정 감정 타입 뱃지
    List<BadgeDefinition> findByEmotionType(EmotionType emotionType);

    // 특정 카테고리 뱃지
    List<BadgeDefinition> findByCategory(String category);

    // 공용 뱃지 (emotionType IS NULL AND category IS NULL)
    List<BadgeDefinition> findByEmotionTypeIsNullAndCategoryIsNull();
}
