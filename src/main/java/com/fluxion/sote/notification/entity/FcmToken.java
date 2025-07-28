package com.fluxion.sote.notification.entity;

import com.fluxion.sote.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 토큰 설정용 생성자
    public FcmToken(String token) {
        this.token = token;
    }

    // User 설정 메서드
    public void setUser(User user) {
        this.user = user;
    }
}
