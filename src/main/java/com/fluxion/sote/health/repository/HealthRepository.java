package com.fluxion.sote.health.repository;

import com.fluxion.sote.health.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HealthRepository extends JpaRepository<HealthData, Long> {

    @Query("SELECT h FROM HealthData h " +
            "WHERE h.userId = :userId AND h.measuredAt BETWEEN :startOfDay AND :endOfDay " +
            "ORDER BY h.measuredAt DESC LIMIT 1")
    Optional<HealthData> findTodayLatestData(
            @Param("userId") Long userId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // 일별 평균값 계산용
    @Query("SELECT DATE(h.measuredAt) AS date, " +
            "AVG(h.heartRate), AVG(h.hrv), AVG(h.steps) " +
            "FROM HealthData h " +
            "WHERE h.userId = :userId AND h.measuredAt >= :startDate " +
            "GROUP BY DATE(h.measuredAt) ORDER BY DATE(h.measuredAt)")
    List<Object[]> findAveragesSince(@Param("userId") Long userId,
                                     @Param("startDate") LocalDateTime startDate);
}
