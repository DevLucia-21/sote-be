package com.fluxion.sote.answer.dto;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

public class QuestionAnswerDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        private String answerText;
        /** 옵션: 특정 월에 저장하려면 "yyyy-MM" 형태 (없으면 서버 현재월 사용) */
        private String month; // e.g., "2025-08"
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateRequest {
        private String answerText; // 수정할 내용
    }
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long questionId;
        private String questionContent;
        private String answerText;
        private Instant answeredAt;
        private LocalDate answerMonth;
        private Integer questionDay;
        private Instant updatedAt;
    }

    /** 월별 내 답변 리스트 최적화용(질문 메타 포함) */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MonthlyItem {
        private Long answerId;
        private Long questionId;
        private String questionContent;
        private Integer questionDay;
        private String answerText;
        private Instant answeredAt;
        private Instant updatedAt;
        private LocalDate date;
    }
}
