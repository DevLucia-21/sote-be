package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FindEmailRequest {

    @NotNull
    private Long userId;            // 사용자 식별용 ID

    @NotNull
    private Integer questionId;     // 보안 질문 ID

    @NotBlank
    private String securityAnswer;  // 보안 질문 답변

    // 기본 생성자
    public FindEmailRequest() {}

    // 전체 필드 생성자
    public FindEmailRequest(Long userId, Integer questionId, String securityAnswer) {
        this.userId = userId;
        this.questionId = questionId;
        this.securityAnswer = securityAnswer;
    }

    // getters / setters

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getQuestionId() {
        return questionId;
    }
    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }
}
