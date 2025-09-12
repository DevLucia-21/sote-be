package com.fluxion.sote.statistics.repository;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface MusicStatisticsRepository extends JpaRepository<AnalysisResult, Long> {

    // 월간 추천된 곡 개수
    @Query("SELECT COUNT(ar) FROM AnalysisResult ar " +
            "JOIN ar.analysis a " +
            "WHERE a.user.id = :userId " +
            "AND ar.selectedTrackTitle IS NOT NULL " +
            "AND ar.createdAt BETWEEN :start AND :end")
    long countMonthlyRecommendedTracks(@Param("userId") Long userId,
                                       @Param("start") OffsetDateTime start,
                                       @Param("end") OffsetDateTime end);

    // 감정별 음악 매칭 (감정 → 장르 분포)
    @Query("SELECT ar.emotionLabel, ar.selectedTrackGenre, COUNT(ar) " +
            "FROM AnalysisResult ar " +
            "JOIN ar.analysis a " +
            "WHERE a.user.id = :userId " +
            "AND ar.selectedTrackGenre IS NOT NULL " +
            "GROUP BY ar.emotionLabel, ar.selectedTrackGenre")
    List<Object[]> countEmotionGenreMapping(@Param("userId") Long userId);
}
