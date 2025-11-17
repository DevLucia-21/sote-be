// src/main/java/com/fluxion/sote/watch/service/WatchSettingsService.java
package com.fluxion.sote.watch.service;

import com.fluxion.sote.watch.dto.WatchSettingsRequestDto;
import com.fluxion.sote.watch.dto.WatchSettingsResponseDto;

public interface WatchSettingsService {

    WatchSettingsResponseDto.SettingsResult getMySettings(Long userId);

    WatchSettingsResponseDto.SettingsResult updateMySettings(
            Long userId,
            WatchSettingsRequestDto.UpdateSettings request
    );
}
