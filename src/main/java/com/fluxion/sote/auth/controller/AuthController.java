// src/main/java/com/fluxion/sote/auth/controller/AuthController.java
package com.fluxion.sote.auth.controller;

import com.fluxion.sote.auth.dto.LoginRequest;
import com.fluxion.sote.auth.dto.SignupRequest;
import com.fluxion.sote.auth.dto.TokenResponse;
import com.fluxion.sote.auth.service.AuthService;
import com.fluxion.sote.global.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        return ResponseUtil.created();              // 201 Created
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseUtil.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String token) {
        return ResponseUtil.ok(authService.refresh(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String token) {
        authService.logout(token);
        return ResponseUtil.ok();                  // 200 OK, no body
    }
}
