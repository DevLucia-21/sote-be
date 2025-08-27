package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FindPwdRequest {

    @NotNull(message = "사용자 ID를 입력해 주세요.")
    private Long userId;            // 사용자 식별용 ID

    @NotNull(message = "보안 질문 ID를 입력해 주세요.")
    private Integer questionId;     // 보안 질문 ID

    @NotBlank(message = "보안 질문 답변을 입력해 주세요.")
    private String securityAnswer;  // 보안 질문 답변

    // 기본 생성자
    public FindPwdRequest() {}

    // 전체 필드 생성자
    public FindPwdRequest(Long userId, Integer questionId, String securityAnswer) {
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
