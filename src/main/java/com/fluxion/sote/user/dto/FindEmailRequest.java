package com.fluxion.sote.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class FindEmailRequest {

    @NotBlank
    private String nickname;        // 사용자 닉네임 (중복 가능)

    @NotNull
    private LocalDate birthDate;    // 생년월일 (추가 식별자)

    @NotNull
    private Integer questionId;     // 보안 질문 ID

    @NotBlank
    private String securityAnswer;  // 보안 질문 답변

    // 기본 생성자
    public FindEmailRequest() {}

    // 전체 필드 생성자
    public FindEmailRequest(String nickname, LocalDate birthDate, Integer questionId, String securityAnswer) {
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.questionId = questionId;
        this.securityAnswer = securityAnswer;
    }

    // getters / setters

    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
