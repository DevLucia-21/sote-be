package com.fluxion.sote.notification.service;

public interface FcmTokenService {

    void saveToken(String token);

    void deleteToken(String token);

    void deleteAllTokensForCurrentUser();

    void registerToken(Long userId, String tokenValue);
}
