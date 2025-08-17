// src/main/java/com/fluxion/sote/question/dto/QuestionDto.java
package com.fluxion.sote.question.dto;

public class QuestionDto {
    private Long id;
    private String content;
    private Integer questionDay;

    public QuestionDto() {}

    public QuestionDto(Long id, String content, Integer questionDay) {
        this.id = id;
        this.content = content;
        this.questionDay = questionDay;

    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Integer getQuestionDay() { return questionDay; }
    public void setQuestionDay(Integer questionDay) { this.questionDay = questionDay; }
}
