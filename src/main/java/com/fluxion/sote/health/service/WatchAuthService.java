// src/main/java/com/fluxion/sote/watch/service/WatchAuthService.java
package com.fluxion.sote.watch.service;

import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.watch.dto.WatchPairCodeResponse;
import com.fluxion.sote.watch.dto.WatchPairLoginRequest;

public interface WatchAuthService {

    // 웹에서 현재 로그인한 사용자 기준으로 페어링 코드 발급
    WatchPairCodeResponse createPairCodeForCurrentUser();

    // 워치에서 코드로 로그인 (토큰 발급)
    TokenResponse loginWithPairCode(WatchPairLoginRequest request);
}
