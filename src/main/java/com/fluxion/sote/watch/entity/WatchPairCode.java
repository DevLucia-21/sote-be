package com.fluxion.sote.watch.entity;

import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "watch_pair_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WatchPairCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 16, nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    // created_at / updated_at 은 지금 안 써도 되니까 생략해도 됩니다.
    // BaseEntity를 쓰고 싶다면 여기서 상속만 추가하면 돼요.
}
