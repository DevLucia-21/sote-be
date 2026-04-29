package com.fluxion.sote.stt.repository;

import com.fluxion.sote.stt.entity.SttResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SttResultRepository extends JpaRepository<SttResult, Long> {

    boolean existsByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );
}