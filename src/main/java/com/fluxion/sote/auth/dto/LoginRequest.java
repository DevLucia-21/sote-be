package com.fluxion.sote.auth.dto;

public record LoginRequest(
        String email,
        String password
) {}
