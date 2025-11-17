package com.fluxion.sote.challenge.entity;

import com.fluxion.sote.analysis.entity.AnalysisResult;
import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChallengeDefinition challenge;

    // 이 챌린지가 어떤 분석 결과(일기 분석)에 의해 추천되었는지 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id")
    private AnalysisResult analysisResult;

    private LocalDate date; // 추천된 날짜

    @Column(name = "is_completed")
    private boolean completed;

    private LocalDateTime completedAt;

    /**
     * 챌린지 완료 처리
     */
    public void complete() {
        if (this.completed) {
            throw new IllegalStateException("이미 완료된 챌린지입니다.");
        }
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }
}
