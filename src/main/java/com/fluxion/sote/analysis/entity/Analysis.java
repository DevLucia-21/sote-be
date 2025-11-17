package com.fluxion.sote.analysis.entity;

import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "analysis",
        uniqueConstraints = @UniqueConstraint(name = "ux_analysis_user_day",
                columnNames = {"user_id", "analysis_date"}
        )
)
@Getter
@Setter
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "birth_year", nullable = false)
    private int birthYear;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToOne(mappedBy = "analysis", fetch = FetchType.LAZY)
    private AnalysisResult result;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "analysis_genres",
            joinColumns = @JoinColumn(name = "analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @PrePersist
    void onCreate() {
        if (analysisDate == null) {
            analysisDate = LocalDate.now(ZoneId.of("Asia/Seoul"));
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // 편의 메서드
    public void attachResult(AnalysisResult r) {
        if (r != null) {
            r.setAnalysis(this);
            this.result = r;
        } else {
            if (this.result != null) {
                this.result.setAnalysis(null);
            }
            this.result = null;
        }
    }
}
