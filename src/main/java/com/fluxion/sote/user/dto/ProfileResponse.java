package com.fluxion.sote.user.dto;

import java.util.List;
import java.util.Set;

public class ProfileResponse {

    private String nickname;
    private String character;
    private String profileImageUrl;
    private int totalDiaryCount;
    private List<String> savedImageUrls;
    private Set<Integer> musicPreferenceIds;

    public ProfileResponse() {
    }

    public ProfileResponse(String nickname, String character, String profileImageUrl, int totalDiaryCount, List<String> savedImageUrls, Set<Integer> musicPreferenceIds ) {
        this.nickname = nickname;
        this.character = character;
        this.profileImageUrl = profileImageUrl;
        this.totalDiaryCount = totalDiaryCount;
        this.savedImageUrls = savedImageUrls;
        this.musicPreferenceIds = musicPreferenceIds;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getTotalDiaryCount() {
        return totalDiaryCount;
    }

    public void setTotalDiaryCount(int totalDiaryCount) {
        this.totalDiaryCount = totalDiaryCount;
    }

    public List<String> getSavedImageUrls() {
        return savedImageUrls;
    }

    public void setSavedImageUrls(List<String> savedImageUrls) {
        this.savedImageUrls = savedImageUrls;
    }

    public Set<Integer> getMusicPreferenceIds() { return musicPreferenceIds; }

    public void setMusicPreferenceIds(Set<Integer> musicPreferenceIds) { this.musicPreferenceIds = musicPreferenceIds; }
}
