// src/main/java/com/fluxion/sote/watch/repository/WatchPairCodeRepository.java
package com.fluxion.sote.watch.repository;

import com.fluxion.sote.watch.entity.WatchPairCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WatchPairCodeRepository extends JpaRepository<WatchPairCode, Long> {

    Optional<WatchPairCode> findByCodeAndUsedFalse(String code);

    long deleteByExpiresAtBefore(LocalDateTime time);
}
