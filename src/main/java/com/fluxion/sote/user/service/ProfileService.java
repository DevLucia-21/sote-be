package com.fluxion.sote.user.service;

import com.fluxion.sote.user.dto.ProfileResponse;
import com.fluxion.sote.user.dto.ProfileUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 로그인한 사용자의 프로필 관련 기능을 제공합니다.
 * - 기본 정보 조회/수정
 * - 프로필 이미지 업로드/삭제
 */
public interface ProfileService {

    /**
     * 로그인한 사용자의 프로필 기본 정보 조회
     * @return ProfileResponse
     */
    ProfileResponse getMyProfile();

    /**
     * 로그인한 사용자의 기본 프로필 정보 수정
     * @return 수정 후 최신 ProfileResponse
     */
    ProfileResponse updateMyProfile(ProfileUpdateRequest request);

    /**
     * 로그인한 사용자의 프로필 이미지 업로드/변경
     * @param image MultipartFile
     */
    void updateProfileImage(MultipartFile image);

    /**
     * 로그인한 사용자의 프로필 이미지 삭제
     */
    void deleteProfileImage();

    /**
     * 로그인한 사용자의 프로필 이미지 바이너리 로드 (이미지 조회용)
     * @return byte[] 이미지 바이트
     */
    byte[] loadMyProfileImage();

    /**
     * 로그인한 사용자의 프로필 이미지 Content-Type 조회 (예: image/png, image/jpeg)
     * @return contentType
     */
    String getMyProfileImageContentType();
}
