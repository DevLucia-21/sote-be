package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.dto.ProfileResponse;
import com.fluxion.sote.user.dto.ProfileUpdateRequest;
import com.fluxion.sote.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024; // 5MB

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = SecurityUtil.getCurrentUser();
        return toResponse(user);
    }

    @Override
    @Transactional
    public ProfileResponse updateMyProfile(ProfileUpdateRequest request) {
        User user = SecurityUtil.getCurrentUser();

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getCharacter() != null) {
            user.setCharacter(request.getCharacter());
        }
        // 이미지 URL은 보통 업로드 엔드포인트에서 관리. 필요 시만 반영
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        Set<Integer> genreIds = request.getGenreIds();
        if (genreIds != null && !genreIds.isEmpty()) {
            var genres = new HashSet<>(genreRepository.findAllById(genreIds));
            user.setMusicPreferences(genres);
        }

        userRepository.save(user);
        // 수정 후 최신 상태 반환
        return toResponse(user);
    }

    @Override
    @Transactional
    public void updateProfileImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new RuntimeException("프로필 이미지가 비어 있습니다.");
        }
        if (image.getSize() > MAX_IMAGE_BYTES) {
            throw new RuntimeException("프로필 이미지가 허용 용량(5MB)을 초과했습니다.");
        }

        // MIME 유형 필터링 (jpeg/png만 허용)
        String contentType = image.getContentType();
        if (contentType != null &&
                !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png"))) {
            throw new RuntimeException("허용되지 않는 이미지 유형입니다. (jpeg/png만 허용)");
        }

        User user = SecurityUtil.getCurrentUser();
        try {
            user.setProfileImage(image.getBytes());
            // DB에 contentType 필드가 없다면, 조회 시 매직넘버로 판별 (아래 getMyProfileImageContentType)
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장 실패", e);
        }
    }

    @Override
    @Transactional
    public void deleteProfileImage() {
        User user = SecurityUtil.getCurrentUser();
        user.setProfileImage(null);
        userRepository.save(user);
    }

    // 추가: 이미지 바이너리 로드 (GET /api/users/profile/image 에서 사용)
    @Override
    @Transactional(readOnly = true)
    public byte[] loadMyProfileImage() {
        User user = SecurityUtil.getCurrentUser();
        byte[] data = user.getProfileImage();
        if (data == null || data.length == 0) {
            throw new RuntimeException("프로필 이미지가 없습니다.");
        }
        return data;
    }

    // 추가: Content-Type 판별 (간단 매직넘버 검사 → 없으면 application/octet-stream)
    @Override
    @Transactional(readOnly = true)
    public String getMyProfileImageContentType() {
        User user = SecurityUtil.getCurrentUser();
        byte[] data = user.getProfileImage();
        if (data == null || data.length == 0) {
            return "application/octet-stream";
        }
        return detectContentType(data);
    }

    // ---- helpers ----
    private ProfileResponse toResponse(User user) {
        int totalDiaryCount = 0;
        List<String> savedImages = List.of();

        Set<Integer> genreIds = user.getMusicPreferences().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        boolean hasBinary = user.getProfileImage() != null && user.getProfileImage().length > 0;
        boolean hasUrl = user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank();

        boolean hasProfileImage = hasBinary || hasUrl;
        String binaryEndpoint = "/api/users/profile/image";

        // profileImageUrl: DB URL 있으면 그대로, 없고 바이너리만 있으면 binaryEndpoint, 없으면 null
        String legacyUrl = hasUrl ? user.getProfileImageUrl() : (hasBinary ? binaryEndpoint : null);

        return new ProfileResponse(
                user.getId(),          // userId
                user.getEmail(),
                user.getNickname(),
                user.getCharacter(),
                hasProfileImage,
                binaryEndpoint,        // imageUrl
                legacyUrl,             // profileImageUrl (DB URL or fallback)
                totalDiaryCount,
                savedImages,
                genreIds
        );
    }

    private String detectContentType(byte[] bytes) {
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && (bytes[1] & 0xFF) == 0x50
                && (bytes[2] & 0xFF) == 0x4E
                && (bytes[3] & 0xFF) == 0x47
                && (bytes[4] & 0xFF) == 0x0D
                && (bytes[5] & 0xFF) == 0x0A
                && (bytes[6] & 0xFF) == 0x1A
                && (bytes[7] & 0xFF) == 0x0A) {
            return "image/png";
        }
        // JPEG: FF D8 FF
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}
