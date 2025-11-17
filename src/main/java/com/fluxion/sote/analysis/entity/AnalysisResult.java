package com.fluxion.sote.analysis.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

// AnalysisResult.java

@Entity
@Table(name = "analysis_result",
        uniqueConstraints = @UniqueConstraint(name = "ux_analysis_result_analysis_id",
                columnNames = {"analysis_id"}))
@Getter @Setter @NoArgsConstructor
public class AnalysisResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false, unique = true)
    private Analysis analysis;

    // emotion summary
    @Column(name = "emotion_label", length = 50)
    private String emotionLabel;

    @Column(name = "emotion_score", precision = 5, scale = 4)
    private BigDecimal emotionScore;

    @Column(name = "emotion_reason", columnDefinition = "TEXT")
    private String emotionReason;

    // selected track (랜덤으로 최종 선택된 1곡 저장)
    @Column(name = "selected_track_title", length = 255)
    private String selectedTrackTitle;

    @Column(name = "selected_track_artist", length = 255)
    private String selectedTrackArtist;

    @Column(name = "selected_track_album", length = 255)
    private String selectedTrackAlbum;

    @Column(name = "selected_track_genre", length = 100)
    private String selectedTrackGenre;

    @Column(name = "selected_track_index")
    private Integer selectedTrackIndex; // 0-based 인덱스

    // 🔥 여기 새로 추가
    @Column(name = "selected_track_reason", columnDefinition = "TEXT")
    private String selectedTrackReason;

    @Column(name = "selected_track_cover_image_url", length = 512)
    private String selectedTrackCoverImageUrl;

    // music 배열 전체 / 원본 응답(JSONB)
    @Column(name = "music_json", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String musicJson;

    @Column(name = "ai_response", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String aiResponse;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now(ZoneId.of("Asia/Seoul"));
        }
    }
}

