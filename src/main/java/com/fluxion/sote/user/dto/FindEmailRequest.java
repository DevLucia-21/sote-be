package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;

public class FindEmailRequest {

    @NotBlank
    private String nickname;      // 회원가입 시 받은 닉네임

    @NotBlank
    private String securityAnswer; // 보안 질문 답변

    // 기본 생성자
    public FindEmailRequest() {}

    public FindEmailRequest(String nickname, String securityAnswer) {
        this.nickname = nickname;
        this.securityAnswer = securityAnswer;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
