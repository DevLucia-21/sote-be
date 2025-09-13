package com.fluxion.sote.lpmusic.entity;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LpReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Diary diary;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 150)
    private String artist;

    @Column(length = 500)
    private String albumImageUrl;

    @Column(length = 500)
    private String playUrl;

    @Column(nullable = false)
    private LocalDateTime recommendedAt;

    @Column(nullable = false)
    private LocalDate rewardDate;

    @PrePersist
    void onCreate() {
        if (recommendedAt == null) {
            recommendedAt = LocalDateTime.now();
        }
        if (rewardDate == null) {
            rewardDate = recommendedAt.toLocalDate();
        }
    }
}
