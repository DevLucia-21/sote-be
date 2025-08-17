// src/main/java/com/fluxion/sote/question/entity/Question.java
package com.fluxion.sote.question.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "question_day", nullable = false)
    private Integer questionDay;// 날짜 전용 컬럼 (1~31)

    public Question() {}

    public Question(String content, Integer questionDay) {
        this.content = content;
        this.questionDay = questionDay;

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getQuestionDay() { return questionDay; }
    public void setQuestionDay(Integer questionDay) { this.questionDay = questionDay; }
}
