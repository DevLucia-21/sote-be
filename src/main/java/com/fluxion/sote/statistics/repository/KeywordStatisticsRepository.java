package com.fluxion.sote.statistics.repository;

import com.fluxion.sote.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface KeywordStatisticsRepository extends JpaRepository<Diary, Long> {

    // 1. 키워드 랭킹 (월간 Top 10)
    @Query("SELECT k.content, COUNT(d) " +
            "FROM Diary d JOIN d.keywords k " +
            "WHERE d.user.id = :userId " +
            "AND d.date BETWEEN :start AND :end " +
            "GROUP BY k.content " +
            "ORDER BY COUNT(d) DESC")
    List<Object[]> findTopKeywordsMonthly(@Param("userId") Long userId,
                                          @Param("start") LocalDate start,
                                          @Param("end") LocalDate end);

    // 2. 기분 랭킹 (기분 → 키워드, 누적)
    @Query("SELECT d.emotionType, k.content, COUNT(d) " +
            "FROM Diary d JOIN d.keywords k " +
            "WHERE d.user.id = :userId " +
            "GROUP BY d.emotionType, k.content")
    List<Object[]> findEmotionToKeyword(@Param("userId") Long userId);

    // 3. 키워드 탐구 (키워드 → 기분, 누적)
    @Query("SELECT k.content, d.emotionType, COUNT(d) " +
            "FROM Diary d JOIN d.keywords k " +
            "WHERE d.user.id = :userId " +
            "GROUP BY k.content, d.emotionType")
    List<Object[]> findKeywordToEmotion(@Param("userId") Long userId);
}
