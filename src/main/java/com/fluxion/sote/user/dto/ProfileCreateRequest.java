// src/main/java/com/fluxion/sote/user/dto/ProfileCreateRequest.java
package com.fluxion.sote.user.dto;

import com.fluxion.sote.global.enums.InstrumentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * 내 프로필 최초 생성 요청 DTO
 * - 회원가입 이후 기본 프로필 저장 시 사용
 * - 모든 값이 필수
 */
public class ProfileCreateRequest {

    @NotNull(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
    private String nickname;

    @NotNull(message = "캐릭터(악기)는 필수입니다.")
    private InstrumentType character;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDate birthDate;

    @Size(max = 2048, message = "이미지 URL이 너무 깁니다.")
    private String profileImageUrl;   // 선택 가능

    @NotNull(message = "genreIds는 필수입니다.")
    private Set<Integer> genreIds;    // 반드시 최소 [] 이상 전달 필요

    // ---------- 생성자 ----------
    public ProfileCreateRequest() {}

    public ProfileCreateRequest(String nickname,
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
