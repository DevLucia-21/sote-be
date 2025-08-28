// src/main/java/com/fluxion/sote/user/service/ProfileServiceImpl.java
package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.exception.ResourceNotFoundException;
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

        // 닉네임
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        // 캐릭터(악기)
        if (request.getCharacter() != null) {
            user.setCharacter(request.getCharacter());
        }

        // 생년월일
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }

        // 이미지 URL
        if (request.getProfileImageUrl() != null) {
            if (request.getProfileImageUrl().isEmpty()) {
                user.setProfileImageUrl(null); // "" 이면 해제 처리
            } else {
                user.setProfileImageUrl(request.getProfileImageUrl());
            }
        }

        // 음악 취향
        Set<Integer> genreIds = request.getGenreIds();
        if (genreIds != null) {
            var genres = new HashSet<>(genreRepository.findAllById(genreIds));
            user.setMusicPreferences(genres);
        }

        userRepository.save(user);
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

        String contentType = image.getContentType();
        if (contentType != null &&
                !(contentType.equalsIgnoreCase("image/jpeg")
                        || contentType.equalsIgnoreCase("image/png"))) {
            throw new RuntimeException("허용되지 않는 이미지 유형입니다. (jpeg/png만 허용)");
        }

        User user = SecurityUtil.getCurrentUser();
        try {
            user.setProfileImage(image.getBytes());
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

        String legacyUrl = hasUrl ? user.getProfileImageUrl() : (hasBinary ? binaryEndpoint : null);

        return new ProfileResponse(
                user.getEmail(),
                user.getNickname(),
                user.getCharacter(),
                user.getBirthDate(),
                hasProfileImage,
                binaryEndpoint,
                legacyUrl,
                totalDiaryCount,
                savedImages,
                genreIds
        );
    }

    private String detectContentType(byte[] bytes) {
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
        if (bytes.length >= 3
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
}
