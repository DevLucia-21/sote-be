// src/main/java/com/fluxion/sote/question/dto/QuestionDto.java
package com.fluxion.sote.question.dto;

public class QuestionDto {
    private Long id;
    private String content;

    public QuestionDto() {}

    public QuestionDto(Long id, String content) {
        this.id = id;
        this.content = content;
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
}
