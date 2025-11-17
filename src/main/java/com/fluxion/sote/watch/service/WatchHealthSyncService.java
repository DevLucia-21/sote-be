// src/main/java/com/fluxion/sote/watch/service/WatchHealthSyncService.java
package com.fluxion.sote.watch.service;

import com.fluxion.sote.watch.dto.WatchHealthSyncResponseDto;

public interface WatchHealthSyncService {

    WatchHealthSyncResponseDto.ForceSyncResult forceSync(Long userId);
}
