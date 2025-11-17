// src/main/java/com/fluxion/sote/watch/dto/WatchLoginTokenResponse.java
package com.fluxion.sote.watch.dto;

import lombok.Builder;

@Builder
public record WatchLoginTokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        Long userId
) {
}
