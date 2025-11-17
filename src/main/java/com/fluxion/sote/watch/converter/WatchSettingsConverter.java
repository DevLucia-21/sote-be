// src/main/java/com/fluxion/sote/watch/converter/WatchSettingsConverter.java
package com.fluxion.sote.watch.converter;

import com.fluxion.sote.watch.dto.WatchSettingsRequestDto;
import com.fluxion.sote.watch.dto.WatchSettingsResponseDto;
import com.fluxion.sote.watch.entity.WatchSettings;
import com.fluxion.sote.auth.entity.User;

public class WatchSettingsConverter {

    public static WatchSettings toEntity(
            User user,
            WatchSettingsRequestDto.UpdateSettings request
    ) {
        return WatchSettings.builder()
                .user(user)
                .notifyHrv(request.getNotifyHrv())
                .notifyHealthSync(request.getNotifyHealthSync())
                .notifyDiary(request.getNotifyDiary())
                .notifyChallenge(request.getNotifyChallenge())
                .wifiOnly(request.getWifiOnly())
                .autoSync(request.getAutoSync())
                .build();
    }

    public static void applyUpdate(
            WatchSettings settings,
            WatchSettingsRequestDto.UpdateSettings request
    ) {
        settings.updateAll(
                request.getNotifyHrv(),
                request.getNotifyHealthSync(),
                request.getNotifyDiary(),
                request.getNotifyChallenge(),
                request.getWifiOnly(),
                request.getAutoSync()
        );
    }

    public static WatchSettingsResponseDto.SettingsResult toSettingsResult(
            WatchSettings settings
    ) {
        return WatchSettingsResponseDto.SettingsResult.builder()
                .notifyHrv(settings.isNotifyHrv())
                .notifyHealthSync(settings.isNotifyHealthSync())
                .notifyDiary(settings.isNotifyDiary())
                .notifyChallenge(settings.isNotifyChallenge())
                .wifiOnly(settings.isWifiOnly())
                .autoSync(settings.isAutoSync())
                .build();
    }

    public static WatchSettingsResponseDto.SettingsResult defaultSettings() {
        return WatchSettingsResponseDto.SettingsResult.builder()
                .notifyHrv(true)
                .notifyHealthSync(true)
                .notifyDiary(true)
                .notifyChallenge(true)
                .wifiOnly(false)
                .autoSync(true)
                .build();
    }
}
