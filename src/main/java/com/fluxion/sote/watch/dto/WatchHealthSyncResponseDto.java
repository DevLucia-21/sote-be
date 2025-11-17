// src/main/java/com/fluxion/sote/watch/dto/response/WatchHealthSyncResponseDto.java
package com.fluxion.sote.watch.dto;

import lombok.Builder;
import lombok.Getter;

public class WatchHealthSyncResponseDto {

    @Getter
    @Builder
    public static class ForceSyncResult {
        private String status;     // 예: "SUCCESS"
        private String syncType;   // 예: "MANUAL"
        private String syncedAt;   // 예: "2025-11-15T16:32:10"
    }
}
