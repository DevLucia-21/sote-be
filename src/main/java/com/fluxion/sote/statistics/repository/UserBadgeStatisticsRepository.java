package com.fluxion.sote.statistics.repository;

import com.fluxion.sote.challenge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBadgeStatisticsRepository extends JpaRepository<UserBadge, Long> {

    // 누적 뱃지 개수
    @Query("SELECT COUNT(b) FROM UserBadge b WHERE b.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
