package com.fluxion.sote.stt.repository;

import com.fluxion.sote.stt.entity.SttResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SttResultRepository extends JpaRepository<SttResult, Long> {

    // 원래 있던 메서드
    boolean existsByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    //전시회용: 오늘 기록 중 가장 최근 한 개 가져오기
    Optional<SttResult> findTopByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}
