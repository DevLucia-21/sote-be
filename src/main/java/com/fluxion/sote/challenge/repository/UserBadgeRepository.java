package com.fluxion.sote.challenge.repository;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.challenge.entity.BadgeDefinition;
import com.fluxion.sote.challenge.entity.UserBadge;
import com.fluxion.sote.global.enums.EmotionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    // 특정 유저의 모든 뱃지 조회 (최신순)
    List<UserBadge> findByUserOrderByAwardedAtDesc(User user);

    // 특정 유저가 이미 해당 뱃지를 가지고 있는지 여부
    boolean existsByUserAndBadgeDefinition(User user, BadgeDefinition badgeDefinition);
}
