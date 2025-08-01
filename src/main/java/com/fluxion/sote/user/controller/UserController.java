package com.fluxion.sote.user.controller;

import com.fluxion.sote.auth.dto.SecurityCheckRequest;
import com.fluxion.sote.auth.dto.SecurityCheckResponse;
import com.fluxion.sote.global.util.ResponseUtil;
import com.fluxion.sote.user.dto.*;
import com.fluxion.sote.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(@Valid @RequestBody FindEmailRequest req) {
        FindEmailResponse resp = userService.findEmail(req);
        return ResponseUtil.ok(resp);
    }

    @PostMapping("/find-pwd")
    public ResponseEntity<FindPwdResponse> findPassword(@Valid @RequestBody FindPwdRequest req) {
        FindPwdResponse resp = userService.findPassword(req);
        return ResponseUtil.ok(resp);
    }

    @PostMapping("/password-reset-temp")
    public ResponseEntity<Void> resetWithTemp(@Valid @RequestBody FindPwdRequest req) {
        userService.resetPasswordWithTemp(req);
        return ResponseUtil.noContent();
    }

    @PostMapping("/check-security")
    public ResponseEntity<SecurityCheckResponse> checkSecurity(@RequestBody SecurityCheckRequest req) {
        boolean ok = userService.checkSecurity(req.getUserId(), req.getQuestionId(), req.getAnswer());
        return ResponseEntity.ok(new SecurityCheckResponse(ok));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(req);
        return ResponseEntity.noContent().build();
    }
}
