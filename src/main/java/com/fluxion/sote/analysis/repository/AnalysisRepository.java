package com.fluxion.sote.analysis.repository;

import com.fluxion.sote.analysis.entity.Analysis;
import com.fluxion.sote.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

    boolean existsByUser_IdAndAnalysisDate(Long userId, LocalDate analysisDate);

    Optional<Analysis> findByUser_IdAndAnalysisDate(Long userId, LocalDate analysisDate);

    Optional<Analysis> findTopByUser_IdOrderByCreatedAtDesc(Long userId);

    List<Analysis> findAllByUser_IdAndAnalysisDateBetweenOrderByAnalysisDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate
    );

    Optional<Analysis> findByUserAndAnalysisDate(User user, LocalDate analysisDate);

    Optional<Analysis> findByUserIdAndDiaryId(Long userId, Long diaryId);
}
