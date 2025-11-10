package com.fluxion.sote.statistics.repository;

import com.fluxion.sote.challenge.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChallengeStatisticsRepository extends JpaRepository<UserChallenge, Long> {

    // 주간 추천된 챌린지 수
    @Query("""
        SELECT COUNT(c)
        FROM UserChallenge c
        WHERE c.user.id = :userId
          AND c.date BETWEEN :start AND :end
    """)
    long countWeeklyChallenges(@Param("userId") Long userId,
                               @Param("start") LocalDate start,
                               @Param("end") LocalDate end);

    // 주간 완료된 챌린지 수
    @Query("""
        SELECT COUNT(c)
        FROM UserChallenge c
        WHERE c.user.id = :userId
          AND c.completed = true
          AND c.date BETWEEN :start AND :end
    """)
    long countWeeklyCompleted(@Param("userId") Long userId,
                              @Param("start") LocalDate start,
                              @Param("end") LocalDate end);

    // 월간 감정별 수행 현황 (완료 수 + 전체 수)
    @Query("""
        SELECT 
            c.challenge.emotionType,
            COUNT(CASE WHEN c.completed = true THEN 1 END) AS completedCount,
            COUNT(c) AS totalCount
        FROM UserChallenge c
        WHERE c.user.id = :userId
          AND c.date BETWEEN :start AND :end
        GROUP BY c.challenge.emotionType
    """)
    List<Object[]> countMonthlyEmotionPerformance(@Param("userId") Long userId,
                                                  @Param("start") LocalDate start,
                                                  @Param("end") LocalDate end);
}
