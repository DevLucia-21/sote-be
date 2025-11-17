package com.fluxion.sote.answer.entity;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.question.entity.Question;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "question_answers",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_q_month",
                columnNames = {"user_id", "question_id", "answer_month"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionAnswer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_text", nullable = false, columnDefinition = "text")
    private String answerText;

    /** UTC 기준 답변 작성 시간 */
    @Column(name = "answered_at", nullable = false)
    private Instant answeredAt;

    /** UTC 기준 수정 시간 */
    @Column(name = "updated_at")
    private Instant updatedAt;

    /** 해당 월의 첫째 날 (예: 2025-08-01) */
    @Column(name = "answer_month", nullable = false)
    private LocalDate answerMonth;
}
