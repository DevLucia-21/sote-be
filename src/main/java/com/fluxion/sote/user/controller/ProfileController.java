package com.fluxion.sote.user.controller;

import com.fluxion.sote.user.dto.ProfileResponse;
import com.fluxion.sote.user.dto.ProfileUpdateRequest;
import com.fluxion.sote.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 내 프로필 조회
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    // 닉네임/캐릭터 등 기본 정보 수정 → 수정 후 최신 프로필 반환
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileResponse updated = profileService.updateMyProfile(request);
        return ResponseEntity.ok(updated);
    }

    // 프로필 이미지 업로드/변경 (multipart/form-data)
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProfileImage(@RequestParam("image") MultipartFile image) {
        String newUrl = profileService.updateProfileImage(image);
        return ResponseEntity.ok(newUrl);
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/image")
    public ResponseEntity<Void> deleteProfileImage() {
        profileService.deleteProfileImage();
        return ResponseEntity.noContent().build();
    }

    // 프로필 이미지 조회 (바이너리)
    @GetMapping("/image")
    public ResponseEntity<byte[]> getProfileImage() {
        byte[] image = profileService.loadMyProfileImage();
        String contentType = profileService.getMyProfileImageContentType();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(image);
    }
}
