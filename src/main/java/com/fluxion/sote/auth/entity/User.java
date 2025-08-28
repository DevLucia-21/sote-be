package com.fluxion.sote.auth.entity;

import com.fluxion.sote.global.enums.InstrumentType;
import com.fluxion.sote.setting.entity.FcmToken;
import com.fluxion.sote.setting.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;

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

    /**
     * 유저 ↔ 장르 다대다 매핑
     * user_genres 조인 테이블을 통해 Genre 엔티티와 연결
     */
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FcmToken> fcmTokens = new ArrayList<>();

    // 중복 방지 토큰 추가
    public void addFcmToken(FcmToken token) {
        boolean exists = fcmTokens.stream()
                .anyMatch(t -> t.getToken().equals(token.getToken()));
        if (!exists) {
            fcmTokens.add(token);
            token.setUser(this);
        }
    }

    // 토큰 제거
    public void removeFcmToken(FcmToken token) {
        fcmTokens.remove(token);
        token.setUser(null);
    }
}
