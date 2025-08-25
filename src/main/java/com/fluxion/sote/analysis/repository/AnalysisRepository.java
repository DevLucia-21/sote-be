package com.fluxion.sote.analysis.repository;

import com.fluxion.sote.analysis.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    /** 하루 1회 제한 체크 (KST 기준 analysisDate 사용) */
    boolean existsByUser_IdAndAnalysisDate(Long userId, LocalDate analysisDate);

    /** 특정 사용자/특정 일자 분석 단건 조회 */
    Optional<Analysis> findByUser_IdAndAnalysisDate(Long userId, LocalDate analysisDate);

    /** 특정 사용자의 가장 최근 분석 */
    Optional<Analysis> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    /** 구간 조회 (예: 최근 3일) */
    List<Analysis> findAllByUser_IdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate
    );
}
