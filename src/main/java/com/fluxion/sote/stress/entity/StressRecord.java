// com.fluxion.sote.stress.entity.StressRecord.java
package com.fluxion.sote.stress.entity;

import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stress_records")
public class StressRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double hrv;

    @Enumerated(EnumType.STRING)
    private StressLevel stressLevel;

    private LocalDateTime measuredAt;

    private LocalDateTime createdAt;
}
