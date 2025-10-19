package com.fluxion.sote.health.entity;

import com.fluxion.sote.global.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "health_data", indexes = {
        @Index(name = "idx_user_measured_at", columnList = "user_id, measured_at")
})
public class HealthData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;        // 사용자 ID

    @Column(name = "heart_rate")
    private Double heartRate;   // 심박수

    @Column(name = "hrv")
    private Double hrv;         // 심박변이도

    @Column(name = "steps")
    private Integer steps;      // 걸음수

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt; // 측정 시각
}
