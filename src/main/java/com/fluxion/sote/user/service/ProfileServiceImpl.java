package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.auth.repository.GenreRepository;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.dto.ProfileResponse;
import com.fluxion.sote.user.dto.ProfileUpdateRequest;
import com.fluxion.sote.user.repository.UserRepository;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024; // 5MB
    private static final String BUCKET_NAME = "sote-profile-image";

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final Storage storage;

    // ==============================
    // 프로필 조회
    // ==============================
    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = SecurityUtil.getCurrentUser();
        return toResponse(user);
    }

    // ==============================
    // 프로필 기본정보 수정
    // ==============================
    @Override
    @Transactional
    public ProfileResponse updateMyProfile(ProfileUpdateRequest request) {
        User user = SecurityUtil.getCurrentUser();

        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getCharacter() != null) user.setCharacter(request.getCharacter());
        if (request.getBirthDate() != null) user.setBirthDate(request.getBirthDate());

        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl().isEmpty() ? null : request.getProfileImageUrl());
        }

        Set<Integer> genreIds = request.getGenreIds();
        if (genreIds != null) {
            var genres = new HashSet<>(genreRepository.findAllById(genreIds));
            user.setMusicPreferences(genres);
        }

        userRepository.save(user);
        return toResponse(user);
    }

    // ==============================
    // 프로필 이미지 업로드/변경 (GCS)
    // ==============================
    @Override
    @Transactional
    public String updateProfileImage(MultipartFile image) {
        if (image == null || image.isEmpty()) throw new RuntimeException("프로필 이미지가 비어 있습니다.");
        if (image.getSize() > MAX_IMAGE_BYTES) throw new RuntimeException("허용 용량(5MB)을 초과했습니다.");

        String contentType = image.getContentType();
        if (contentType != null &&
                !(contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/png"))) {
            throw new RuntimeException("jpeg/png만 허용됩니다.");
        }

        User user = SecurityUtil.getCurrentUser();

        // 기존 이미지 삭제 (기존 URL 있으면 GCS에서 제거)
        deleteImageFromGcs(user.getProfileImageUrl());

        // 고유 파일명 생성
        String ext = getExtension(image.getOriginalFilename());
        String fileName = "profile_" + user.getId() + "_" + UUID.randomUUID() + ext;

        try {
            BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(image.getContentType())
                    .build();

            storage.create(blobInfo, image.getBytes());

            // 공개 URL 생성
            String publicUrl = String.format("https://storage.googleapis.com/%s/%s", BUCKET_NAME, fileName);

            // DB에 URL 저장
            user.setProfileImageUrl(publicUrl);
            userRepository.save(user);

            // 컨트롤러에 URL 반환
            return publicUrl;

        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 실패", e);
        }
    }

    // ==============================
    // 프로필 이미지 삭제
    // ==============================
    @Override
    @Transactional
    public void deleteProfileImage() {
        User user = SecurityUtil.getCurrentUser();
        deleteImageFromGcs(user.getProfileImageUrl());
        user.setProfileImageUrl(null);
        userRepository.save(user);
    }

    private void deleteImageFromGcs(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/")) return;
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        storage.delete(BlobId.of(BUCKET_NAME, fileName));
    }

    // ==============================
    // GCS 이미지 조회 (옵션)
    // ==============================
    @Override
    @Transactional(readOnly = true)
    public byte[] loadMyProfileImage() {
        User user = SecurityUtil.getCurrentUser();
        if (user.getProfileImageUrl() == null) throw new RuntimeException("프로필 이미지가 없습니다.");

        String fileName = user.getProfileImageUrl().substring(user.getProfileImageUrl().lastIndexOf("/") + 1);
        return storage.readAllBytes(BlobId.of(BUCKET_NAME, fileName));
    }

    @Override
    @Transactional(readOnly = true)
    public String getMyProfileImageContentType() {
        User user = SecurityUtil.getCurrentUser();
        if (user.getProfileImageUrl() == null) return "application/octet-stream";
        String fileName = user.getProfileImageUrl().substring(user.getProfileImageUrl().lastIndexOf("/") + 1);
        var blob = storage.get(BlobId.of(BUCKET_NAME, fileName));
        return blob != null ? blob.getContentType() : "application/octet-stream";
    }

    // ==============================
    // 헬퍼 메서드
    // ==============================
    private ProfileResponse toResponse(User user) {
        int totalDiaryCount = 0;
        List<String> savedImages = List.of();

        Set<Integer> genreIds = user.getMusicPreferences().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        boolean hasProfileImage = user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank();
        String binaryEndpoint = "/api/users/profile/image";

        return new ProfileResponse(
                user.getEmail(),
                user.getNickname(),
                user.getCharacter(),
                user.getBirthDate(),
                hasProfileImage,
                binaryEndpoint,
                user.getProfileImageUrl(),
                totalDiaryCount,
                savedImages,
                genreIds
        );
    }

    private String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf('.'));
        }
        return ".png";
    }
}
