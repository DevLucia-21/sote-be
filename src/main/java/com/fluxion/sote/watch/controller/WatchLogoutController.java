// src/main/java/com/fluxion/sote/watch/controller/WatchLogoutController.java
package com.fluxion.sote.watch.controller;

import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.watch.dto.WatchLogoutRequestDto;
import com.fluxion.sote.watch.dto.WatchLogoutResponseDto;
import com.fluxion.sote.watch.service.WatchLogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch/auth")
@RequiredArgsConstructor
public class WatchLogoutController {

    private final WatchLogoutService watchLogoutService;

    @PostMapping("/logout")
    public ResponseEntity<WatchLogoutResponseDto.LogoutResult> logoutFromWatch(
            @RequestBody(required = false) @Valid WatchLogoutRequestDto.Logout request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        WatchLogoutResponseDto.LogoutResult result =
                watchLogoutService.logoutFromWatch(userId, request);
        return ResponseEntity.ok(result);
    }
}
