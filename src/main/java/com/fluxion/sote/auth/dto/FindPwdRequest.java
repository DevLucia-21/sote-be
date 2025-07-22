package com.fluxion.sote.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class FindPwdRequest {

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    @NotBlank(message = "보안 질문 답변을 입력해 주세요.")
    private String securityAnswer;

    public FindPwdRequest() {}

    public FindPwdRequest(String email, String securityAnswer) {
        this.email = email;
        this.securityAnswer = securityAnswer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
