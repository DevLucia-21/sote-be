package com.fluxion.sote.auth.service;

import com.fluxion.sote.auth.dto.*;

public interface AuthService {
    void signup(SignupRequest req);
    TokenResponse login(LoginRequest req);
    TokenResponse refresh(String refreshToken);
    void logout(String refreshToken);
}
