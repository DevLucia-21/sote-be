package com.fluxion.sote.user.dto;

import com.fluxion.sote.global.enums.InstrumentType;

import java.util.List;
import java.util.Set;

/**
 * 내 프로필 조회 응답
 * - imageUrl: 서버 바이너리 엔드포인트(/api/users/profile/image)로 직접 접근하기 위한 경로
 * - hasProfileImage: 이미지 존재 여부
 * - profileImageUrl(legacy): DB에 저장된 문자열 URL (있으면 우선 사용, 없으면 imageUrl 사용)
 */
public class ProfileResponse {

    private Long userId;
    private String email;
    private String nickname;
    private InstrumentType character;

    private boolean hasProfileImage;
    private String imageUrl;             // 예: "/api/users/profile/image"
    private String profileImageUrl;      // DB에 저장된 URL (legacy)

    private int totalDiaryCount;
    private List<String> savedImageUrls;
    private Set<Integer> musicPreferenceIds;

    public ProfileResponse() {
    }

    /**
     * 권장 생성자 (서비스에서 모든 값 확정 후 전달)
     */
    public ProfileResponse(Long userId,
                           String email,
                           String nickname,
                           InstrumentType character,
                           boolean hasProfileImage,
                           String imageUrl,
                           String profileImageUrl,
                           int totalDiaryCount,
                           List<String> savedImageUrls,
                           Set<Integer> musicPreferenceIds) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.character = character;
        this.hasProfileImage = hasProfileImage;
        this.imageUrl = imageUrl;
        this.profileImageUrl = profileImageUrl;
        this.totalDiaryCount = totalDiaryCount;
        this.savedImageUrls = savedImageUrls;
        this.musicPreferenceIds = musicPreferenceIds;
    }

    // ---------- getters / setters ----------

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public InstrumentType getCharacter() { return character; }
    public void setCharacter(InstrumentType character) { this.character = character; }

    public boolean isHasProfileImage() { return hasProfileImage; }
    public void setHasProfileImage(boolean hasProfileImage) { this.hasProfileImage = hasProfileImage; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public int getTotalDiaryCount() { return totalDiaryCount; }
    public void setTotalDiaryCount(int totalDiaryCount) { this.totalDiaryCount = totalDiaryCount; }

    public List<String> getSavedImageUrls() { return savedImageUrls; }
    public void setSavedImageUrls(List<String> savedImageUrls) { this.savedImageUrls = savedImageUrls; }

    public Set<Integer> getMusicPreferenceIds() { return musicPreferenceIds; }
    public void setMusicPreferenceIds(Set<Integer> musicPreferenceIds) { this.musicPreferenceIds = musicPreferenceIds; }
}
