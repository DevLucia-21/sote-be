// src/main/java/com/fluxion/sote/setting/service/FcmTokenService.java
package com.fluxion.sote.setting.service;

import com.fluxion.sote.setting.enums.DeviceType;

public interface FcmTokenService {

    void saveToken(String token, DeviceType deviceType);

    void saveToken(String token);

    void registerToken(Long userId, String tokenValue, DeviceType deviceType);

    void registerToken(Long userId, String tokenValue);

    void deleteToken(String token);

    void deleteAllTokensForCurrentUser();

    void deleteExpiredToken(String token);
}
