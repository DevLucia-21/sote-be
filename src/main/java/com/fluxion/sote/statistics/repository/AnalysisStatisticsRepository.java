package com.fluxion.sote.statistics.repository;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnalysisStatisticsRepository extends JpaRepository<AnalysisResult, Long> {

    // 유저별 감정 라벨 분포 (누적)
    @Query("SELECT ar.emotionLabel, COUNT(ar) " +
            "FROM AnalysisResult ar " +
            "JOIN ar.analysis a " +
            "WHERE a.user.id = :userId " +
            "GROUP BY ar.emotionLabel")
    List<Object[]> countEmotionDistributionByUserId(@Param("userId") Long userId);
}
