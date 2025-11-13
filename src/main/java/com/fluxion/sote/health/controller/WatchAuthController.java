// src/main/java/com/fluxion/sote/watch/controller/WatchAuthController.java
package com.fluxion.sote.watch.controller;

import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.global.util.ResponseUtil;
import com.fluxion.sote.watch.dto.WatchPairCodeResponse;
import com.fluxion.sote.watch.dto.WatchPairLoginRequest;
import com.fluxion.sote.watch.service.WatchAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch/auth")
@RequiredArgsConstructor
public class WatchAuthController {

    private final WatchAuthService watchAuthService;

    /**
     * 웹에서 호출: 현재 로그인한 사용자의 워치 페어링 코드 발급
     */
    @PostMapping("/pair-code")
    public ResponseEntity<WatchPairCodeResponse> createPairCode() {
        WatchPairCodeResponse response = watchAuthService.createPairCodeForCurrentUser();
        return ResponseUtil.ok(response);
    }

    /**
     * 워치에서 호출: 페어링 코드로 로그인, 토큰 발급
     */
    @PostMapping("/pair")
    public ResponseEntity<TokenResponse> pair(@RequestBody WatchPairLoginRequest request) {
        TokenResponse response = watchAuthService.loginWithPairCode(request);
        return ResponseUtil.ok(response);
    }
}
