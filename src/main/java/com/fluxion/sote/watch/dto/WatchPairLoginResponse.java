// com.fluxion.sote.watch.dto.WatchPairLoginResponse.java
package com.fluxion.sote.watch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WatchPairLoginResponse {

    private String accessToken;
    private String refreshToken;

    private Long userId;
    private String nickname;
}
