// src/main/java/com/fluxion/sote/user/dto/ProfileUpdateRequest.java
package com.fluxion.sote.user.dto;

import com.fluxion.sote.global.enums.InstrumentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * 내 프로필 수정 요청 DTO
 * - 닉네임, 캐릭터(악기), 생년월일, 이미지, 음악 취향 수정 가능
 * - null 값은 "변경 없음" 의미
 * - profileImageUrl: ""(빈 문자열) 이면 해제 처리
 * - genreIds: [] → 모두 제거, 값 있음 → 교체
 */
public class ProfileUpdateRequest {

    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;               // null이면 변경 없음

    private InstrumentType character;      // null이면 변경 없음

    private LocalDate birthDate;           // null이면 변경 없음

    @Size(max = 2048, message = "이미지 URL이 너무 깁니다.")
    private String profileImageUrl;        // null이면 변경 없음, ""로 보내면 해제 처리

    @NotNull(message = "genreIds는 null일 수 없습니다. 빈 배열은 허용됩니다.")
    private Set<Integer> genreIds;         // [] → 모두 제거, 값 있음 → 교체

    // ---------- 생성자 ----------
    public ProfileUpdateRequest() {}

    public ProfileUpdateRequest(String nickname,
                                InstrumentType character,
                                LocalDate birthDate,
                                String profileImageUrl,
                                Set<Integer> genreIds) {
        this.nickname = nickname;
        this.character = character;
        this.birthDate = birthDate;
        this.profileImageUrl = profileImageUrl;
        this.genreIds = genreIds;
    }

    // ---------- getters / setters ----------
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public InstrumentType getCharacter() { return character; }
    public void setCharacter(InstrumentType character) { this.character = character; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Set<Integer> getGenreIds() { return genreIds; }
    public void setGenreIds(Set<Integer> genreIds) { this.genreIds = genreIds; }
}
