package com.fluxion.sote.challenge.entity;

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

    private LocalDate date; // 추천된 날짜

    @Column(name = "is_completed")  // DB 컬럼명은 is_completed 유지
    private boolean completed;

    private LocalDateTime completedAt; // 실제 완료 시각

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
