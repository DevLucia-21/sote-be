package com.fluxion.sote.statistics.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.fluxion.sote.diary.entity.Diary;

public interface DiaryStatisticsRepository extends JpaRepository<Diary, Long> {

    // 누적 작성 수
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.user.id = :userId")
    long countTotalByUserId(@Param("userId") Long userId);

    // 월간 작성 수
    @Query("SELECT COUNT(d) FROM Diary d " +
            "WHERE d.user.id = :userId " +
            "AND YEAR(d.date) = :year " +
            "AND MONTH(d.date) = :month")
    long countMonthlyByUserId(@Param("userId") Long userId,
                              @Param("year") int year,
                              @Param("month") int month);
}
