package com.fluxion.sote.challenge.entity;

import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private ChallengeDefinition challenge;

    private LocalDate date; // 수행 날짜

    private boolean isCompleted;

    public void complete() {
        this.isCompleted = true;
    }
}
