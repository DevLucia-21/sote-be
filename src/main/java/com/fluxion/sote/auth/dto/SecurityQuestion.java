// src/main/java/com/fluxion/sote/auth/dto/SecurityQuestionDto.java
package com.fluxion.sote.auth.dto;

public class SecurityQuestion {
    private Integer id;
    private String questionText;

    public SecurityQuestion() { }

    public SecurityQuestion(Integer id, String questionText) {
        this.id = id;
        this.questionText = questionText;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
