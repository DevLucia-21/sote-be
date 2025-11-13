// src/main/java/com/fluxion/sote/watch/dto/WatchPairCodeResponse.java
package com.fluxion.sote.watch.dto;

import java.time.LocalDateTime;

public record WatchPairCodeResponse(
        String code,
        LocalDateTime expiresAt
) {}
