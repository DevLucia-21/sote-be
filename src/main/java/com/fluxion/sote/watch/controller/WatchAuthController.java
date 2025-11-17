package com.fluxion.sote.watch.controller;

import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.util.ResponseUtil;
import com.fluxion.sote.watch.dto.WatchLoginTokenResponse;
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

    private final WatchAuthService service;

    /**
     * 앱 → 서버 : 페어링 코드 생성
     */
    @PostMapping("/pair-code")
    public ResponseEntity<WatchPairCodeResponse> createPairCode(
            @RequestAttribute("user") User user
    ) {
        WatchPairCodeResponse response = service.createPairCodeForUser(user);
        return ResponseUtil.ok(response);
    }

    /**
     * 워치 → 서버 : 코드 인증하고 로그인
     */
    @PostMapping("/pair")
    public ResponseEntity<WatchLoginTokenResponse> loginWithCode(
            @RequestBody WatchPairLoginRequest request
    ) {
        TokenResponse token = service.loginWithPairCode(request);

        return ResponseEntity.ok(
                new WatchLoginTokenResponse(
                        token.accessToken(),
                        token.refreshToken(),
                        token.expiresIn(),
                        token.userId()
                )
        );
    }
}