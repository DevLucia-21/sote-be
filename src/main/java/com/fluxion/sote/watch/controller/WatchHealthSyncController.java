// src/main/java/com/fluxion/sote/watch/controller/WatchHealthSyncController.java
package com.fluxion.sote.watch.controller;

import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.watch.dto.WatchHealthSyncResponseDto;
import com.fluxion.sote.watch.service.WatchHealthSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch/health-sync")
@RequiredArgsConstructor
public class WatchHealthSyncController {

    private final WatchHealthSyncService watchHealthSyncService;

    @PostMapping("/force")
    public ResponseEntity<WatchHealthSyncResponseDto.ForceSyncResult> forceSync() {
        Long userId = SecurityUtil.getCurrentUserId();
        WatchHealthSyncResponseDto.ForceSyncResult result =
                watchHealthSyncService.forceSync(userId);
        return ResponseEntity.ok(result);
    }
}
