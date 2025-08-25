package com.fluxion.sote.user.dto;

import com.fluxion.sote.global.enums.InstrumentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class ProfileUpdateRequest {

    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;               // null이면 변경 없음

    private InstrumentType character;      // null이면 변경 없음

    @Size(max = 2048, message = "이미지 URL이 너무 깁니다.")
    private String profileImageUrl;        // null이면 변경 없음, ""로 보내면 해제 처리

    @NotNull(message = "genreIds는 null일 수 없습니다. 빈 배열은 허용됩니다.")
    private Set<Integer> genreIds;         // [] → 모두 제거, 값 있음 → 교체

    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String nickname,
                                InstrumentType character,
                                String profileImageUrl,
                                Set<Integer> genreIds) {
        this.nickname = nickname;
        this.character = character;
        this.profileImageUrl = profileImageUrl;
        this.genreIds = genreIds;
    }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public InstrumentType getCharacter() { return character; }
    public void setCharacter(InstrumentType character) { this.character = character; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Set<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(Set<Integer> genreIds) { this.genreIds = genreIds; }
}
