// src/main/java/com/fluxion/sote/watch/entity/WatchPairCode.java
package com.fluxion.sote.watch.entity;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.global.model.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "watch_pair_codes",
        indexes = {
                @Index(name = "idx_watch_pair_code_code", columnList = "code")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchPairCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 6자리 코드 (예: 483927)
    @Column(name = "code", nullable = false, unique = true, length = 16)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 만료 시각
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // 사용 여부
    @Column(name = "used", nullable = false)
    private boolean used;

    // 실제 사용된 시각
    @Column(name = "used_at")
    private LocalDateTime usedAt;
}
