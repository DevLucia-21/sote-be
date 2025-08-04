package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public class ProfileUpdateRequest {

    private String nickname;
    private String character;
    private String profileImageUrl;
    @NotNull
    private Set<Integer> genreIds;

    public ProfileUpdateRequest() {
    }

    public ProfileUpdateRequest(String nickname, String character, String profileImageUrl,Set<Integer> genreIds) {
        this.nickname = nickname;
        this.character = character;
        this.profileImageUrl = profileImageUrl;
        this.genreIds = genreIds;
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

    public Set<Integer> getGenreIds() { return genreIds; }

    public void setGenreIds(Set<Integer> genreIds) { this.genreIds = genreIds; }
}
