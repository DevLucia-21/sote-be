package com.fluxion.sote.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class FindPwdRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
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
