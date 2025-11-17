// src/main/java/com/fluxion/sote/watch/dto/response/WatchLogoutResponseDto.java
package com.fluxion.sote.watch.dto;

import lombok.Builder;
import lombok.Getter;

public class WatchLogoutResponseDto {

    @Getter
    @Builder
    public static class LogoutResult {
        private String message;   // 예: "WATCH_LOGOUT_SUCCESS"
    }
}
