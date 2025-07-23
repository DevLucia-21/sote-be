package com.fluxion.sote.auth.controller;

import com.fluxion.sote.auth.dto.*;
import com.fluxion.sote.auth.service.AuthService;
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
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody String token) {
        return ResponseEntity.ok(authService.refresh(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(@Valid @RequestBody FindEmailRequest req) {
        return ResponseEntity.ok(authService.findEmail(req));
    }

    @PostMapping("/find-pwd")
    public ResponseEntity<FindPwdResponse> findPassword(
            @Valid @RequestBody FindPwdRequest req) {
        return ResponseEntity.ok(authService.findPassword(req));
    }

    /**
     * 보안 질문이 일치하는 회원에게 임시 비밀번호를 발급해 이메일로 전송합니다.
     */
    @PostMapping("/password-reset-temp")
    public ResponseEntity<Void> resetWithTemp(
            @Valid @RequestBody FindPwdRequest req) {
        authService.resetPasswordWithTemp(req);
        return ResponseEntity.ok().build();
    }
}
