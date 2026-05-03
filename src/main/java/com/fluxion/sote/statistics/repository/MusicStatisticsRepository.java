package com.fluxion.sote.statistics.repository;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MusicStatisticsRepository extends JpaRepository<AnalysisResult, Long> {

    // 월간 추천된 곡 개수
    // createdAt이 아니라 analysisDate 기준으로 집계
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar " +
            "JOIN ar.analysis a " +
            "WHERE a.user.id = :userId " +
            "AND ar.selectedTrackTitle IS NOT NULL " +
            "AND a.analysisDate BETWEEN :start AND :end")
    long countMonthlyRecommendedTracks(@Param("userId") Long userId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    // 월간 감정별 음악 매칭
    // createdAt이 아니라 analysisDate 기준으로 집계
    @Query("SELECT ar.emotionLabel, ar.selectedTrackGenre, COUNT(ar) " +
            "FROM AnalysisResult ar " +
            "JOIN ar.analysis a " +
            "WHERE a.user.id = :userId " +
            "AND ar.selectedTrackGenre IS NOT NULL " +
            "AND a.analysisDate BETWEEN :start AND :end " +
            "GROUP BY ar.emotionLabel, ar.selectedTrackGenre")
    List<Object[]> countMonthlyEmotionGenreMapping(@Param("userId") Long userId,
                                                   @Param("start") LocalDate start,
                                                   @Param("end") LocalDate end);

    // 전체 누적 감정별 음악 매칭
    @Query("SELECT ar.emotionLabel, ar.selectedTrackGenre, COUNT(ar) " +
            "FROM AnalysisResult ar " +
            "JOIN ar.analysis a " +
            "WHERE a.user.id = :userId " +
            "AND ar.selectedTrackGenre IS NOT NULL " +
            "GROUP BY ar.emotionLabel, ar.selectedTrackGenre")
    List<Object[]> countEmotionGenreMapping(@Param("userId") Long userId);
}