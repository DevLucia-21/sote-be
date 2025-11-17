// src/main/java/com/fluxion/sote/watch/scheduler/WatchPairCodeCleanupScheduler.java
package com.fluxion.sote.watch.scheduler;

import com.fluxion.sote.watch.repository.WatchPairCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class WatchPairCodeCleanupScheduler {

    private final WatchPairCodeRepository watchPairCodeRepository;

    /**
     * 매 시간 정각마다 만료된 페어링 코드를 삭제한다.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanExpiredPairCodes() {
        LocalDateTime now = LocalDateTime.now();
        long deleted = watchPairCodeRepository.deleteByExpiresAtBefore(now);
        if (deleted > 0) {
            log.info("Deleted {} expired watch pair codes at {}", deleted, now);
        }
    }
}
