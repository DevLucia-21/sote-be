package com.fluxion.sote.auth.dto;

public class SecurityCheckRequest {
    private String email;
    private Integer questionId;
    private String answer;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}

