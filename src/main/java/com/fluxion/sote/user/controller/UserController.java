// src/main/java/com/fluxion/sote/user/controller/UserController.java
package com.fluxion.sote.user.controller;

import com.fluxion.sote.global.util.ResponseUtil;
import com.fluxion.sote.user.dto.FindEmailRequest;
import com.fluxion.sote.user.dto.FindEmailResponse;
import com.fluxion.sote.user.dto.FindPwdRequest;
import com.fluxion.sote.user.dto.FindPwdResponse;
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

    /**
     * 닉네임과 보안 질문 답변으로 이메일 조회
     * POST /api/users/find-email
     */
    @PostMapping("/find-email")
    public ResponseEntity<FindEmailResponse> findEmail(
            @Valid @RequestBody FindEmailRequest req) {
        FindEmailResponse resp = userService.findEmail(req);
        return ResponseUtil.ok(resp);
    }

    /**
     * 이메일과 보안 질문 답변으로 비밀번호 해시 조회
     * POST /api/users/find-pwd
     */
    @PostMapping("/find-pwd")
    public ResponseEntity<FindPwdResponse> findPassword(
            @Valid @RequestBody FindPwdRequest req) {
        FindPwdResponse resp = userService.findPassword(req);
        return ResponseUtil.ok(resp);
    }

    /**
     * 보안 질문이 일치하는 회원에게 임시 비밀번호를 발급해 이메일로 전송합니다.
     * POST /api/users/password-reset-temp
     */
    @PostMapping("/password-reset-temp")
    public ResponseEntity<Void> resetWithTemp(
            @Valid @RequestBody FindPwdRequest req) {
        userService.resetPasswordWithTemp(req);
        return ResponseUtil.noContent();  // 204 No Content
    }
}
