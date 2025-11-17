package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FindPwdRequest {

    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    @NotNull(message = "보안 질문 ID를 입력해 주세요.")
    private Integer questionId;

    @NotBlank(message = "보안 질문 답변을 입력해 주세요.")
    private String securityAnswer;

    // 기본 생성자
    public FindPwdRequest() {}

    // 전체 필드 생성자
    public FindPwdRequest(String email, Integer questionId, String securityAnswer) {
        this.email = email;
        this.questionId = questionId;
        this.securityAnswer = securityAnswer;
    }

    // getters / setters

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getSecurityAnswer() { return securityAnswer; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
}
