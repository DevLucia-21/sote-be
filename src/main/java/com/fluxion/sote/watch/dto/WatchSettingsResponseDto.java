// src/main/java/com/fluxion/sote/watch/dto/response/WatchSettingsResponseDto.java
package com.fluxion.sote.watch.dto;

import lombok.Builder;
import lombok.Getter;

public class WatchSettingsResponseDto {

    @Getter
    @Builder
    public static class SettingsResult {
        private boolean notifyHrv;
        private boolean notifyHealthSync;
        private boolean notifyDiary;
        private boolean notifyChallenge;
        private boolean wifiOnly;
        private boolean autoSync;
    }
}
