package com.fluxion.sote.health.repository;

import com.fluxion.sote.health.entity.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HealthRepository extends JpaRepository<HealthData, Long> {

    // 오늘 최신 1건
    Optional<HealthData> findTopByUserIdAndMeasuredAtBetweenOrderByMeasuredAtDesc(
            Long userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    // 멱등 저장을 위한 키 조회
    Optional<HealthData> findByUserIdAndMeasuredAt(Long userId, LocalDateTime measuredAt);

    // 일자별 평균 (네이티브)
    @Query(value = """
        SELECT DATE(measured_at)   AS d,
               AVG(heart_rate)     AS avg_hr,
               AVG(hrv)            AS avg_hrv,
               AVG(steps)          AS avg_steps
          FROM health_data
         WHERE user_id = :userId
           AND measured_at >= :startDate
         GROUP BY DATE(measured_at)
         ORDER BY DATE(measured_at)
        """, nativeQuery = true)
    List<Object[]> findAveragesSinceNative(@Param("userId") Long userId,
                                           @Param("startDate") LocalDateTime startDate);
}
