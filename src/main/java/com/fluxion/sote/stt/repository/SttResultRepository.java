package com.fluxion.sote.stt.repository;

import com.fluxion.sote.stt.entity.SttResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SttResultRepository extends JpaRepository<SttResult, Long> {

    // 오늘 1회 제한 체크
    boolean existsByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

}
