package com.fluxion.sote.user.dto;

import com.fluxion.sote.global.enums.InstrumentType;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 내 프로필 조회 응답
 * - imageUrl: 서버 바이너리 엔드포인트(/api/users/profile/image)로 직접 접근하기 위한 경로
 * - hasProfileImage: 이미지 존재 여부
 * - profileImageUrl(legacy): DB에 저장된 문자열 URL (있으면 우선 사용, 없으면 imageUrl 사용)
 */
public class ProfileResponse {

    private String email;
    private String nickname;
    private InstrumentType character;
    private LocalDate birthDate;

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
    public ProfileResponse(
                           String email,
                           String nickname,
                           InstrumentType character,
                           LocalDate birthDate,
                           boolean hasProfileImage,
                           String imageUrl,
                           String profileImageUrl,
                           int totalDiaryCount,
                           List<String> savedImageUrls,
                           Set<Integer> musicPreferenceIds) {
        this.email = email;
        this.nickname = nickname;
        this.character = character;
        this.birthDate = birthDate;
        this.hasProfileImage = hasProfileImage;
        this.imageUrl = imageUrl;
        this.profileImageUrl = profileImageUrl;
        this.totalDiaryCount = totalDiaryCount;
        this.savedImageUrls = savedImageUrls;
        this.musicPreferenceIds = musicPreferenceIds;
    }

    // ---------- getters / setters ----------

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public InstrumentType getCharacter() { return character; }
    public void setCharacter(InstrumentType character) { this.character = character; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

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
