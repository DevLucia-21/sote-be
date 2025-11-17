// src/main/java/com/fluxion/sote/watch/dto/request/WatchSettingsRequestDto.java
package com.fluxion.sote.watch.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

public class WatchSettingsRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateSettings {

        @NotNull
        private Boolean notifyHrv;

        @NotNull
        private Boolean notifyHealthSync;

        @NotNull
        private Boolean notifyDiary;

        @NotNull
        private Boolean notifyChallenge;

        @NotNull
        private Boolean wifiOnly;

        @NotNull
        private Boolean autoSync;
    }
}
