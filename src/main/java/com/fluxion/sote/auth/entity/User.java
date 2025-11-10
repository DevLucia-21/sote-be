// src/main/java/com/fluxion/sote/auth/entity/User.java
package com.fluxion.sote.auth.entity;

import com.fluxion.sote.global.enums.InstrumentType;
import com.fluxion.sote.setting.entity.FcmToken;
import com.fluxion.sote.setting.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_genres",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> musicPreferences = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "character", nullable = false, length = 20)
    private InstrumentType character = InstrumentType.PIANO;

    @Column(name = "profile_image", columnDefinition = "bytea")
    private byte[] profileImage;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "dark_mode", nullable = false)
    private boolean darkMode = false;

    @ElementCollection(targetClass = NotificationType.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_notifications", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private Set<NotificationType> enabledNotifications = new HashSet<>();

    /**
     * 유저 ↔ FcmToken (1:N)
     * cascade + orphanRemoval로 User 삭제 시 관련 토큰 자동 제거
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

    //중복 방지 + 양방향 관계 유지
    public void addFcmToken(FcmToken token) {
        boolean exists = fcmTokens.stream()
                .anyMatch(t -> t.getToken().equals(token.getToken()));
        if (!exists) {
            token.setUser(this); // 자동으로 fcmTokens에도 추가됨
        }
    }

    // 토큰 제거 (양방향 관계 해제)
    public void removeFcmToken(FcmToken token) {
        token.setUser(null); // setUser에서 fcmTokens.remove(this)까지 처리됨
    }
}
