// src/main/java/com/fluxion/sote/watch/service/WatchLogoutService.java
package com.fluxion.sote.watch.service;

import com.fluxion.sote.watch.dto.WatchLogoutRequestDto;
import com.fluxion.sote.watch.dto.WatchLogoutResponseDto;

public interface WatchLogoutService {

    WatchLogoutResponseDto.LogoutResult logoutFromWatch(
            Long userId,
            WatchLogoutRequestDto.Logout request
    );
}
