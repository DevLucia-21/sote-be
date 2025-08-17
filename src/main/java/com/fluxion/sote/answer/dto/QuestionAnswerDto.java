package com.fluxion.sote.answer.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class QuestionAnswerDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateRequest {
        private String answerText;
        /** 옵션: 특정 월에 저장하려면 "yyyy-MM" 형태 (없으면 서버 현재월 사용) */
        private String month; // e.g., "2025-08"
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long questionId;
        private String questionContent;
        private String answerText;
        private LocalDateTime answeredAt;
        private LocalDate answerMonth;
        private Integer questionDay;
    }

    /** 월별 내 답변 리스트 최적화용(질문 메타 포함) */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MonthlyItem {
        private Long answerId;
        private Long questionId;
        private String questionContent;
        private Integer questionDay;      // Question.day (1~30)
        private String answerText;
        private LocalDateTime answeredAt;
        private LocalDate date;
    }
}
