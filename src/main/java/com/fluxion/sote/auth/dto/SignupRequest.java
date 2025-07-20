package com.fluxion.sote.auth.dto;

public record SignupRequest(
        String email,
        String password,
        String nickname
) {}
