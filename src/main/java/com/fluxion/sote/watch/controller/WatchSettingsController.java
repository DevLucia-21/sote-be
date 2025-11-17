// src/main/java/com/fluxion/sote/watch/controller/WatchSettingsController.java
package com.fluxion.sote.watch.controller;

import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.watch.dto.WatchSettingsRequestDto;
import com.fluxion.sote.watch.dto.WatchSettingsResponseDto;
import com.fluxion.sote.watch.service.WatchSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watch/settings")
@RequiredArgsConstructor
public class WatchSettingsController {

    private final WatchSettingsService watchSettingsService;

    @GetMapping
    public ResponseEntity<WatchSettingsResponseDto.SettingsResult> getMySettings() {
        Long userId = SecurityUtil.getCurrentUserId();
        WatchSettingsResponseDto.SettingsResult result = watchSettingsService.getMySettings(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<WatchSettingsResponseDto.SettingsResult> updateMySettings(
            @RequestBody @Valid WatchSettingsRequestDto.UpdateSettings request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        WatchSettingsResponseDto.SettingsResult result = watchSettingsService.updateMySettings(userId, request);
        return ResponseEntity.ok(result);
    }
}
