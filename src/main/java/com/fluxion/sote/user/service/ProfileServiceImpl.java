// src/main/java/com/fluxion/sote/user/service/ProfileServiceImpl.java
package com.fluxion.sote.user.service;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.util.SecurityUtil;
import com.fluxion.sote.user.dto.ProfileResponse;
import com.fluxion.sote.user.dto.ProfileUpdateRequest;
import com.fluxion.sote.auth.repository.GenreRepository;
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

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;

    @Override
    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile() {
        User user = SecurityUtil.getCurrentUser();

        // 일기 수 및 저장 이미지가 없다면 기본값
        int totalDiaryCount = 0;
        List<String> savedImages = List.of();

        Set<Integer> genreIds = user.getMusicPreferences().stream()
                .map(g -> g.getId())
                .collect(Collectors.toSet());

        return new ProfileResponse(
                user.getNickname(),
                user.getCharacter(),
                user.getProfileImageUrl(),
                totalDiaryCount,
                savedImages,
                genreIds
        );
    }

    @Override
    @Transactional
    public void updateMyProfile(ProfileUpdateRequest request) {
        User user = SecurityUtil.getCurrentUser();
        user.setNickname(request.getNickname());
        user.setCharacter(request.getCharacter());
        user.setProfileImageUrl(request.getProfileImageUrl());

        Set<Integer> genreIds = request.getGenreIds();
        if (genreIds != null && !genreIds.isEmpty()) {
            var genres = new HashSet<>(genreRepository.findAllById(genreIds));
            user.setMusicPreferences(genres);
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateProfileImage(MultipartFile image) {
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
}
