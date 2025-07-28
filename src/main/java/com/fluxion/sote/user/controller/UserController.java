// src/main/java/com/fluxion/sote/user/controller/UserController.java
package com.fluxion.sote.user.controller;

import com.fluxion.sote.auth.dto.SecurityCheckRequest;
import com.fluxion.sote.auth.dto.SecurityCheckResponse;
import com.fluxion.sote.global.util.ResponseUtil;
import com.fluxion.sote.user.dto.*;
import com.fluxion.sote.user.service.KeywordService;
import com.fluxion.sote.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fluxion.sote.user.dto.UserProfileResponse;
import com.fluxion.sote.user.dto.UserProfileUpdateRequest;
import com.fluxion.sote.user.dto.KeywordResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final KeywordService keywordService;

    public UserController(UserService userService, KeywordService keywordService) {
        this.userService = userService;
        this.keywordService = keywordService;
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

    @PostMapping("/check-security")
    public ResponseEntity<SecurityCheckResponse> checkSecurity(
            @RequestBody SecurityCheckRequest req) {
        boolean ok = userService.checkSecurity(
                req.getUserId(), req.getQuestionId(), req.getAnswer());
        return ResponseEntity.ok(new SecurityCheckResponse(ok));
    }

    @GetMapping("/users/me")
    public UserProfileResponse getMyProfile() {
        return userService.getMyProfile();
    }

    @PutMapping("/users/me")
    public void updateMyProfile(@RequestBody UserProfileUpdateRequest request) {
        userService.updateMyProfile(request);
    }

    /**
     * 키워드 목록 조회
     */
    @GetMapping("/keywords")
    public List<KeywordResponse> getKeywords() {
        return keywordService.getKeywords();
    }

    /**
     * 키워드 등록
     */
    @PostMapping("/keywords")
    public void addKeyword(@RequestBody String content) {
        keywordService.addKeyword(content);
    }

    /**
     * 키워드 삭제
     */
    @DeleteMapping("/keywords/{id}")
    public void deleteKeyword(@PathVariable Long id) {
        keywordService.deleteKeyword(id);
    }

    @GetMapping("/settings")
    public UserSettingsResponse getMySettings() {
        return userService.getUserSettings();
    }

    @PutMapping("/settings")
    public void updateMySettings(@RequestBody UserSettingsRequest request) {
        userService.updateUserSettings(request);
    }

    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateProfileImage(@RequestPart MultipartFile image) {
        userService.updateProfileImage(image);
    }

    @DeleteMapping("/profile/image")
    public void deleteProfileImage() {
        userService.deleteProfileImage();
    }
}
