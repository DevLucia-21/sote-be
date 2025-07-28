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

    @GetMapping("/users/me")
    public UserProfileResponse getMyProfile() {
        return userService.getMyProfile();
    }

    @PutMapping("/users/me")
    public void updateMyProfile(@RequestBody UserProfileUpdateRequest request) {
        userService.updateMyProfile(request);
    }

    @PutMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateProfileImage(@RequestPart MultipartFile image) {
        userService.updateProfileImage(image);
    }

    @DeleteMapping("/profile/image")
    public void deleteProfileImage() {
        userService.deleteProfileImage();
    }

    @GetMapping("/keywords")
    public List<KeywordResponse> getKeywords() {
        return keywordService.getKeywords();
    }

    @PostMapping("/keywords")
    public void addKeyword(@RequestBody String content) {
        keywordService.addKeyword(content);
    }

    @DeleteMapping("/keywords/{id}")
    public void deleteKeyword(@PathVariable Long id) {
        keywordService.deleteKeyword(id);
    }
}
