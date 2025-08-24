package com.fluxion.sote.analysis.entity;

import com.fluxion.sote.auth.entity.Genre;
import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.diary.entity.Diary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "analysis",
        uniqueConstraints = @UniqueConstraint(name = "ux_analysis_user_day",
                columnNames = {"user_id", "analysis_date"}))
@Getter @Setter
public class Analysis {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)               // FK → users.id
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)               // FK → diaries.id (nullable)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "birth_year", nullable = false)
    private int birthYear;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // 결과 1:1 (analysis_result) — 조회 편의용 양방향 추가
    @OneToOne(mappedBy = "analysis", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private AnalysisResult result;

    // 장르 다대다: analysis_genres 조인 테이블
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "analysis_genres",
            joinColumns = @JoinColumn(name = "analysis_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @PrePersist
    void onCreate() {
        ZoneId KST = ZoneId.of("Asia/Seoul");
        if (analysisDate == null) analysisDate = LocalDate.now(KST);
        if (createdAt == null) createdAt = OffsetDateTime.now(KST);
    }

    // 편의 메서드 (양방향 연동)
    public void attachResult(AnalysisResult r) {
        if (r != null) {
            r.setAnalysis(this);
            this.result = r;
        } else {
            if (this.result != null) this.result.setAnalysis(null);
            this.result = null;
        }
    }
}
