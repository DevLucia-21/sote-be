package com.fluxion.sote.auth.dto;

public class SecurityAnswer {
    private Integer questionId;
    private String answer;

    // getters / setters
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
