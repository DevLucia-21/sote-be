package com.fluxion.sote.auth.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        Long userId
) {}
