// src/main/java/com/fluxion/sote/setting/entity/FcmToken.java
package com.fluxion.sote.setting.entity;

import com.fluxion.sote.auth.entity.User;
import com.fluxion.sote.setting.enums.DeviceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fcm_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false)
    private DeviceType deviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 양방향 관계 편의 메서드
     * User ↔ FcmToken 관계를 동기화하기 위해 사용
     */
    public void setUser(User user) {
        // 이미 같은 유저면 중복 설정 방지
        if (this.user == user) return;

        // 기존 user가 존재하면 연결 제거
        if (this.user != null) {
            this.user.getFcmTokens().remove(this);
        }

        // 새 유저 설정 및 역참조 추가
        this.user = user;
        if (user != null && !user.getFcmTokens().contains(this)) {
            user.getFcmTokens().add(this);
        }
    }
}
